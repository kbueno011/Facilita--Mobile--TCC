package com.exemple.facilita.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.exemple.facilita.data.api.*
import com.exemple.facilita.data.models.*
import com.exemple.facilita.repository.PagBankRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CarteiraViewModel : ViewModel() {

    private val _saldo = MutableStateFlow(SaldoCarteira(0.0, 0.0, 0.0))
    val saldo: StateFlow<SaldoCarteira> = _saldo.asStateFlow()

    private val _transacoes = MutableStateFlow<List<TransacaoCarteira>>(emptyList())
    val transacoes: StateFlow<List<TransacaoCarteira>> = _transacoes.asStateFlow()

    private val _cartoesSalvos = MutableStateFlow<List<CartaoSalvo>>(emptyList())
    val cartoesSalvos: StateFlow<List<CartaoSalvo>> = _cartoesSalvos.asStateFlow()

    private val _contasBancarias = MutableStateFlow<List<ContaBancaria>>(emptyList())
    val contasBancarias: StateFlow<List<ContaBancaria>> = _contasBancarias.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _ultimaTransacao = MutableStateFlow<TransacaoCarteira?>(null)
    val ultimaTransacao: StateFlow<TransacaoCarteira?> = _ultimaTransacao.asStateFlow()

    private val _pixQrCode = MutableStateFlow<String?>(null)
    val pixQrCode: StateFlow<String?> = _pixQrCode.asStateFlow()

    private val _pixQrCodeBase64 = MutableStateFlow<String?>(null)
    val pixQrCodeBase64: StateFlow<String?> = _pixQrCodeBase64.asStateFlow()

    // Repositório PagBank
    private val pagBankRepository = PagBankRepository()

    private val carteiraApi: CarteiraApiService

    init {
        // Inicializa API local (adaptar para seu base URL)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.facilita.com/") // Substituir pela sua URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        carteiraApi = retrofit.create(CarteiraApiService::class.java)

        // Inicia com saldo zerado - sem dados simulados
        _saldo.value = SaldoCarteira(
            saldoDisponivel = 0.0,
            saldoBloqueado = 0.0,
            saldoTotal = 0.0
        )

        // Inicia sem transações
        _transacoes.value = emptyList()

        // Cartões e contas podem continuar para testes
        _cartoesSalvos.value = listOf(
            CartaoSalvo(
                id = "1",
                ultimos4Digitos = "4321",
                bandeira = "Visa",
                nomeCompleto = "João Silva",
                validade = "12/2025",
                isPrincipal = true
            )
        )

        _contasBancarias.value = listOf(
            ContaBancaria(
                id = "1",
                banco = "Banco do Brasil",
                agencia = "1234-5",
                conta = "12345-6",
                tipoConta = "CORRENTE",
                nomeCompleto = "João Silva",
                cpf = "123.456.789-00",
                isPrincipal = true
            )
        )
    }

    // Removido carregarDadosSimulados()

    fun carregarSaldo(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = carteiraApi.obterSaldo("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _saldo.value = response.body()!!
                } else {
                    _error.value = "Erro ao carregar saldo"
                }
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "Erro ao carregar saldo", e)
                _error.value = "Erro de conexão"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun carregarTransacoes(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = carteiraApi.obterTransacoes("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    _transacoes.value = response.body()!!
                } else {
                    _error.value = "Erro ao carregar transações"
                }
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "Erro ao carregar transações", e)
                _error.value = "Erro de conexão"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun depositarViaPix(
        token: String,
        valor: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _pixQrCode.value = null
            _pixQrCodeBase64.value = null

            try {
                val referenceId = "DEP_PIX_${System.currentTimeMillis()}"

                Log.d("CarteiraViewModel", "Iniciando depósito PIX - Valor: R$ $valor")

                val result = pagBankRepository.criarCobrancaPix(
                    referenceId = referenceId,
                    valor = valor,
                    descricao = "Depósito na carteira Facilita"
                )

                result.fold(
                    onSuccess = { chargeResponse ->
                        Log.d("CarteiraViewModel", "Cobrança PIX criada: ${chargeResponse.id}")

                        // Extrai QR Code do PIX
                        val pixResponse = chargeResponse.paymentMethod?.pix
                        if (pixResponse != null) {
                            _pixQrCode.value = pixResponse.qrCode
                            _pixQrCodeBase64.value = pixResponse.qrCodeBase64

                            // Registra transação pendente
                            val novaTransacao = TransacaoCarteira(
                                id = chargeResponse.id,
                                tipo = TipoTransacao.DEPOSITO,
                                valor = valor,
                                descricao = "Depósito via PIX",
                                data = "Agora",
                                status = StatusTransacao.PENDENTE,
                                metodo = MetodoPagamento.PIX,
                                referenciaPagBank = chargeResponse.id
                            )

                            _ultimaTransacao.value = novaTransacao

                            // Adiciona à lista de transações
                            _transacoes.value = listOf(novaTransacao) + _transacoes.value

                            Log.d("CarteiraViewModel", "QR Code PIX gerado com sucesso")
                            onSuccess()
                        } else {
                            val erro = "QR Code PIX não disponível na resposta"
                            Log.e("CarteiraViewModel", erro)
                            onError(erro)
                        }
                    },
                    onFailure = { exception ->
                        val erro = exception.message ?: "Erro desconhecido ao criar cobrança PIX"
                        Log.e("CarteiraViewModel", erro, exception)
                        onError(erro)
                    }
                )
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "Erro ao depositar via PIX", e)
                onError("Erro de conexão: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun limparPixQrCode() {
        _pixQrCode.value = null
        _pixQrCodeBase64.value = null
    }

    fun confirmarPagamentoPix(valor: Double) {
        // Atualiza o saldo
        _saldo.value = _saldo.value.copy(
            saldoDisponivel = _saldo.value.saldoDisponivel + valor,
            saldoTotal = _saldo.value.saldoTotal + valor
        )

        // Atualiza a transação PENDENTE para CONCLUIDO ao invés de criar nova
        val transacoesAtualizadas = _transacoes.value.map { transacao ->
            if (transacao.status == StatusTransacao.PENDENTE &&
                transacao.tipo == TipoTransacao.DEPOSITO &&
                transacao.metodo == MetodoPagamento.PIX &&
                transacao.valor == valor) {
                // Encontrou a transação pendente, atualiza para CONCLUIDO
                transacao.copy(
                    status = StatusTransacao.CONCLUIDO,
                    data = "Agora"
                )
            } else {
                transacao
            }
        }

        _transacoes.value = transacoesAtualizadas

        // Limpa o QR Code
        limparPixQrCode()

        Log.d("CarteiraViewModel", "✅ Pagamento PIX confirmado - Valor: R$ $valor")
        Log.d("CarteiraViewModel", "✅ Novo saldo: R$ ${_saldo.value.saldoDisponivel}")
        Log.d("CarteiraViewModel", "✅ Total de transações: ${_transacoes.value.size}")
    }

    fun depositarViaCartao(
        token: String,
        valor: Double,
        numeroCartao: String,
        mesExpiracao: String,
        anoExpiracao: String,
        cvv: String,
        nomeCompleto: String,
        parcelamento: Int = 1,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val referenceId = "DEP_CARD_${System.currentTimeMillis()}"

                Log.d("CarteiraViewModel", "Iniciando depósito via cartão - Valor: R$ $valor")

                val result = pagBankRepository.criarCobrancaCartao(
                    referenceId = referenceId,
                    valor = valor,
                    descricao = "Depósito na carteira Facilita",
                    numeroCartao = numeroCartao,
                    mesExpiracao = mesExpiracao,
                    anoExpiracao = anoExpiracao,
                    cvv = cvv,
                    nomeCompleto = nomeCompleto,
                    parcelamento = parcelamento
                )

                result.fold(
                    onSuccess = { chargeResponse ->
                        Log.d("CarteiraViewModel", "Cobrança cartão criada: ${chargeResponse.id}")

                        // Verifica o status
                        when (chargeResponse.status) {
                            "AUTHORIZED", "PAID" -> {
                                // Pagamento aprovado
                                val novaTransacao = TransacaoCarteira(
                                    id = chargeResponse.id,
                                    tipo = TipoTransacao.DEPOSITO,
                                    valor = valor,
                                    descricao = "Depósito via Cartão de Crédito",
                                    data = "Agora",
                                    status = StatusTransacao.CONCLUIDO,
                                    metodo = MetodoPagamento.CARTAO_CREDITO,
                                    referenciaPagBank = chargeResponse.id
                                )

                                _ultimaTransacao.value = novaTransacao

                                // Atualiza saldo
                                _saldo.value = _saldo.value.copy(
                                    saldoDisponivel = _saldo.value.saldoDisponivel + valor,
                                    saldoTotal = _saldo.value.saldoTotal + valor
                                )

                                // Adiciona transação à lista
                                _transacoes.value = listOf(novaTransacao) + _transacoes.value

                                Log.d("CarteiraViewModel", "Depósito via cartão concluído")
                                onSuccess()
                            }
                            "DECLINED" -> {
                                Log.e("CarteiraViewModel", "Cartão recusado")
                                onError("Cartão recusado. Verifique os dados ou use outro cartão.")
                            }
                            else -> {
                                Log.e("CarteiraViewModel", "Status desconhecido: ${chargeResponse.status}")
                                onError("Status da transação: ${chargeResponse.status}")
                            }
                        }
                    },
                    onFailure = { exception ->
                        val erro = exception.message ?: "Erro ao processar pagamento"
                        Log.e("CarteiraViewModel", erro, exception)
                        onError(erro)
                    }
                )
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "Erro ao depositar via cartão", e)
                onError("Erro de conexão: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sacar(
        token: String,
        valor: Double,
        contaBancariaId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                if (valor > _saldo.value.saldoDisponivel) {
                    onError("Saldo insuficiente")
                    _isLoading.value = false
                    return@launch
                }

                // MODO SIMULADO - Funciona sem backend
                Log.d("CarteiraViewModel", "⚠️ MODO SIMULADO - Processando saque")
                delay(1500) // Simula delay da API

                // Cria a transação
                val transacao = TransacaoCarteira(
                    id = "SAQUE_${System.currentTimeMillis()}",
                    tipo = TipoTransacao.SAQUE,
                    valor = -valor,
                    descricao = "Transferência para conta bancária",
                    data = "Agora",
                    status = StatusTransacao.CONCLUIDO,
                    metodo = null
                )

                // Atualiza saldo
                _saldo.value = _saldo.value.copy(
                    saldoDisponivel = _saldo.value.saldoDisponivel - valor,
                    saldoTotal = _saldo.value.saldoTotal - valor
                )

                // Adiciona transação à lista
                _transacoes.value = listOf(transacao) + _transacoes.value

                _ultimaTransacao.value = transacao

                Log.d("CarteiraViewModel", "✅ Saque simulado concluído - Valor: R$ $valor")
                Log.d("CarteiraViewModel", "✅ Novo saldo: R$ ${_saldo.value.saldoDisponivel}")

                onSuccess()
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "Erro ao realizar saque", e)
                onError("Erro ao processar saque: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun adicionarCartao(
        token: String,
        request: CartaoRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = carteiraApi.adicionarCartao("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    _cartoesSalvos.value = _cartoesSalvos.value + response.body()!!
                    onSuccess()
                } else {
                    onError("Erro ao adicionar cartão")
                }
            } catch (e: Exception) {
                onError("Erro de conexão: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removerCartao(token: String, cartaoId: String) {
        viewModelScope.launch {
            try {
                val response = carteiraApi.removerCartao("Bearer $token", cartaoId)
                if (response.isSuccessful) {
                    _cartoesSalvos.value = _cartoesSalvos.value.filter { it.id != cartaoId }
                }
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "Erro ao remover cartão", e)
            }
        }
    }

    fun adicionarContaBancaria(
        token: String,
        request: ContaBancariaRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = carteiraApi.adicionarContaBancaria("Bearer $token", request)
                if (response.isSuccessful && response.body() != null) {
                    _contasBancarias.value = _contasBancarias.value + response.body()!!
                    onSuccess()
                } else {
                    onError("Erro ao adicionar conta bancária")
                }
            } catch (e: Exception) {
                onError("Erro de conexão: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removerContaBancaria(token: String, contaId: String) {
        viewModelScope.launch {
            try {
                val response = carteiraApi.removerContaBancaria("Bearer $token", contaId)
                if (response.isSuccessful) {
                    _contasBancarias.value = _contasBancarias.value.filter { it.id != contaId }
                }
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "Erro ao remover conta bancária", e)
            }
        }
    }

    fun limparErro() {
        _error.value = null
    }

    fun adicionarContaBancariaLocal(
        banco: String,
        agencia: String,
        conta: String,
        tipoConta: String,
        nomeCompleto: String,
        cpf: String,
        isPrincipal: Boolean
    ) {
        val novaConta = ContaBancaria(
            id = "CONTA_${System.currentTimeMillis()}",
            banco = banco,
            agencia = agencia,
            conta = conta,
            tipoConta = tipoConta,
            nomeCompleto = nomeCompleto,
            cpf = cpf,
            isPrincipal = isPrincipal
        )

        // Se for principal, remove a flag das outras
        val contasAtualizadas = if (isPrincipal) {
            _contasBancarias.value.map { it.copy(isPrincipal = false) } + novaConta
        } else {
            _contasBancarias.value + novaConta
        }

        _contasBancarias.value = contasAtualizadas

        Log.d("CarteiraViewModel", "✅ Conta bancária adicionada: $banco - Ag: $agencia - Conta: $conta")
        Log.d("CarteiraViewModel", "✅ Total de contas: ${_contasBancarias.value.size}")
    }
}

