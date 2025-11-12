package com.exemple.facilita.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.exemple.facilita.data.models.*
import com.exemple.facilita.repository.CarteiraLocalRepository
import com.exemple.facilita.repository.PagBankRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarteiraViewModel(application: Application) : AndroidViewModel(application) {

    private val localRepository = CarteiraLocalRepository(application.applicationContext)
    private val pagBankRepository = PagBankRepository()

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

    private val _pixQrCode = MutableStateFlow<String?>(null)
    val pixQrCode: StateFlow<String?> = _pixQrCode.asStateFlow()

    private val _pixQrCodeBase64 = MutableStateFlow<String?>(null)
    val pixQrCodeBase64: StateFlow<String?> = _pixQrCodeBase64.asStateFlow()

    init {
        carregarDadosLocais()
        Log.d("CarteiraViewModel", "✅ ViewModel inicializado com persistência local")
    }

    private fun carregarDadosLocais() {
        try {
            val saldoSalvo = localRepository.obterSaldo()
            _saldo.value = saldoSalvo

            val transacoesSalvas = localRepository.obterTransacoes()
            _transacoes.value = transacoesSalvas

            Log.d("CarteiraViewModel", "📊 Dados carregados: Saldo=R$ ${saldoSalvo.saldoDisponivel}, Transações=${transacoesSalvas.size}")
        } catch (e: Exception) {
            Log.e("CarteiraViewModel", "❌ Erro ao carregar dados locais", e)
        }
    }

    fun carregarSaldo(token: String) {
        carregarDadosLocais()
    }

    fun carregarTransacoes(token: String) {
        _transacoes.value = localRepository.obterTransacoes()
    }

    // DEPÓSITO SIMULADO (para testes)
    fun depositarSimulado(valor: Double) {
        viewModelScope.launch {
            try {
                val novoSaldo = localRepository.adicionarSaldo(valor)
                _saldo.value = novoSaldo

                val transacao = localRepository.criarTransacaoDeposito(
                    valor = valor,
                    metodo = MetodoPagamento.PIX,
                    referenciaPagBank = null
                ).copy(status = StatusTransacao.CONCLUIDO)

                localRepository.salvarTransacao(transacao)
                _transacoes.value = localRepository.obterTransacoes()

                Log.d("CarteiraViewModel", "✅ Depósito simulado: R$ $valor")
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "❌ Erro no depósito simulado", e)
            }
        }
    }

    // DÉBITO PARA PAGAMENTO DE SERVIÇO
    fun debitarParaServico(
        valorServico: Double,
        servicoId: String,
        descricaoServico: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("CarteiraViewModel", "🔄 Debitando R$ $valorServico para serviço $servicoId")

                val resultado = localRepository.debitarSaldo(valorServico)

                resultado.fold(
                    onSuccess = { novoSaldo ->
                        _saldo.value = novoSaldo

                        val transacao = localRepository.criarTransacaoDebito(
                            valor = valorServico,
                            descricao = descricaoServico,
                            servicoId = servicoId
                        )

                        localRepository.salvarTransacao(transacao)
                        _transacoes.value = localRepository.obterTransacoes()

                        Log.d("CarteiraViewModel", "✅ Débito realizado - Novo saldo: R$ ${novoSaldo.saldoDisponivel}")
                        onSuccess()
                    },
                    onFailure = { exception ->
                        Log.e("CarteiraViewModel", "❌ Falha ao debitar: ${exception.message}")
                        onError(exception.message ?: "Saldo insuficiente")
                    }
                )
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "❌ Erro ao debitar", e)
                onError("Erro ao processar débito: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ADICIONAR CONTA BANCÁRIA LOCAL
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
            id = System.currentTimeMillis().toString(),
            banco = banco,
            agencia = agencia,
            conta = conta,
            tipoConta = tipoConta,
            nomeCompleto = nomeCompleto,
            cpf = cpf,
            isPrincipal = isPrincipal
        )

        _contasBancarias.value = _contasBancarias.value + novaConta
        Log.d("CarteiraViewModel", "✅ Conta bancária adicionada: $banco")
    }

    fun limparTodosDados() {
        localRepository.limparDados()
        carregarDadosLocais()
    }

    // DEPÓSITO VIA PIX (integração PagBank)
    fun depositarViaPix(
        token: String,
        valor: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _pixQrCode.value = null
            _pixQrCodeBase64.value = null

            try {
                val referenceId = "DEP_PIX_${System.currentTimeMillis()}"
                Log.d("CarteiraViewModel", "🔄 Iniciando depósito PIX - Valor: R$ $valor")

                val result = pagBankRepository.criarCobrancaPix(
                    referenceId = referenceId,
                    valor = valor,
                    descricao = "Depósito na carteira Facilita"
                )

                result.fold(
                    onSuccess = { chargeResponse ->
                        Log.d("CarteiraViewModel", "✅ Cobrança PIX criada: ${chargeResponse.id}")

                        val pixResponse = chargeResponse.paymentMethod?.pix
                        if (pixResponse != null) {
                            _pixQrCode.value = pixResponse.qrCode
                            _pixQrCodeBase64.value = pixResponse.qrCodeBase64

                            val novaTransacao = localRepository.criarTransacaoDeposito(
                                valor = valor,
                                metodo = MetodoPagamento.PIX,
                                referenciaPagBank = chargeResponse.id
                            )

                            localRepository.salvarTransacao(novaTransacao)
                            _transacoes.value = localRepository.obterTransacoes()

                            Log.d("CarteiraViewModel", "✅ QR Code PIX gerado com sucesso")
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
                Log.e("CarteiraViewModel", "❌ Erro ao depositar via PIX", e)
                onError("Erro de conexão: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun confirmarPagamentoPix(valor: Double) {
        viewModelScope.launch {
            try {
                val novoSaldo = localRepository.adicionarSaldo(valor)
                _saldo.value = novoSaldo

                val transacoes = localRepository.obterTransacoes()
                val transacaoPendente = transacoes.find {
                    it.status == StatusTransacao.PENDENTE &&
                    it.tipo == TipoTransacao.DEPOSITO &&
                    it.metodo == MetodoPagamento.PIX &&
                    it.valor == valor
                }

                if (transacaoPendente != null) {
                    localRepository.atualizarStatusTransacao(
                        transacaoPendente.id,
                        StatusTransacao.CONCLUIDO
                    )
                }

                _transacoes.value = localRepository.obterTransacoes()
                _pixQrCode.value = null
                _pixQrCodeBase64.value = null

                Log.d("CarteiraViewModel", "✅ Pagamento PIX confirmado - Novo saldo: R$ ${novoSaldo.saldoDisponivel}")
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "❌ Erro ao confirmar pagamento PIX", e)
            }
        }
    }

    // DEPÓSITO VIA CARTÃO
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

            try {
                val referenceId = "DEP_CARD_${System.currentTimeMillis()}"
                Log.d("CarteiraViewModel", "🔄 Iniciando depósito via cartão - Valor: R$ $valor")

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
                        Log.d("CarteiraViewModel", "✅ Cobrança cartão criada: ${chargeResponse.id}")

                        when (chargeResponse.status) {
                            "AUTHORIZED", "PAID" -> {
                                val novoSaldo = localRepository.adicionarSaldo(valor)
                                _saldo.value = novoSaldo

                                val transacao = localRepository.criarTransacaoDeposito(
                                    valor = valor,
                                    metodo = MetodoPagamento.CARTAO_CREDITO,
                                    referenciaPagBank = chargeResponse.id
                                ).copy(status = StatusTransacao.CONCLUIDO)

                                localRepository.salvarTransacao(transacao)
                                _transacoes.value = localRepository.obterTransacoes()

                                Log.d("CarteiraViewModel", "✅ Depósito via cartão concluído")
                                onSuccess()
                            }
                            "DECLINED" -> {
                                Log.e("CarteiraViewModel", "❌ Cartão recusado")
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
                Log.e("CarteiraViewModel", "❌ Erro ao depositar via cartão", e)
                onError("Erro de conexão: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // SAQUE
    fun sacar(
        token: String,
        valor: Double,
        contaBancariaId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                Log.d("CarteiraViewModel", "🔄 Solicitando saque de R$ $valor")

                val resultado = localRepository.debitarSaldo(valor)

                resultado.fold(
                    onSuccess = { novoSaldo ->
                        _saldo.value = novoSaldo

                        val transacao = TransacaoCarteira(
                            id = "SAQ_${System.currentTimeMillis()}",
                            tipo = TipoTransacao.SAQUE,
                            valor = valor,
                            descricao = "Saque para conta bancária",
                            data = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale("pt", "BR")).format(java.util.Date()),
                            status = StatusTransacao.CONCLUIDO,
                            metodo = null,
                            referenciaPagBank = contaBancariaId
                        )

                        localRepository.salvarTransacao(transacao)
                        _transacoes.value = localRepository.obterTransacoes()

                        Log.d("CarteiraViewModel", "✅ Saque realizado com sucesso")
                        onSuccess()
                    },
                    onFailure = { exception ->
                        Log.e("CarteiraViewModel", "❌ Falha ao sacar: ${exception.message}")
                        onError(exception.message ?: "Saldo insuficiente")
                    }
                )
            } catch (e: Exception) {
                Log.e("CarteiraViewModel", "❌ Erro ao sacar", e)
                onError("Erro ao processar saque: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}

