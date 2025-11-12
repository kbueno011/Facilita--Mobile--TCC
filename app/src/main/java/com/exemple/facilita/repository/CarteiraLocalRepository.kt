package com.exemple.facilita.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.exemple.facilita.data.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class CarteiraLocalRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "carteira_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        private const val KEY_SALDO_DISPONIVEL = "saldo_disponivel"
        private const val KEY_SALDO_BLOQUEADO = "saldo_bloqueado"
        private const val KEY_TRANSACOES = "transacoes"
        private const val KEY_ULTIMA_ATUALIZACAO = "ultima_atualizacao"
    }

    // ==================== SALDO ====================

    fun salvarSaldo(saldo: SaldoCarteira) {
        prefs.edit().apply {
            putString(KEY_SALDO_DISPONIVEL, saldo.saldoDisponivel.toString())
            putString(KEY_SALDO_BLOQUEADO, saldo.saldoBloqueado.toString())
            putString(KEY_ULTIMA_ATUALIZACAO, System.currentTimeMillis().toString())
            apply()
        }
        Log.d("CarteiraLocal", "Saldo salvo: Disponível=${saldo.saldoDisponivel}, Bloqueado=${saldo.saldoBloqueado}")
    }

    fun obterSaldo(): SaldoCarteira {
        val disponivel = prefs.getString(KEY_SALDO_DISPONIVEL, "0.0")?.toDoubleOrNull() ?: 0.0
        val bloqueado = prefs.getString(KEY_SALDO_BLOQUEADO, "0.0")?.toDoubleOrNull() ?: 0.0
        return SaldoCarteira(
            saldoDisponivel = disponivel,
            saldoBloqueado = bloqueado,
            saldoTotal = disponivel + bloqueado
        )
    }

    fun adicionarSaldo(valor: Double): SaldoCarteira {
        val saldoAtual = obterSaldo()
        val novoSaldo = SaldoCarteira(
            saldoDisponivel = saldoAtual.saldoDisponivel + valor,
            saldoBloqueado = saldoAtual.saldoBloqueado,
            saldoTotal = saldoAtual.saldoTotal + valor
        )
        salvarSaldo(novoSaldo)
        Log.d("CarteiraLocal", "Saldo adicionado: +$valor | Novo saldo: ${novoSaldo.saldoDisponivel}")
        return novoSaldo
    }

    fun debitarSaldo(valor: Double): Result<SaldoCarteira> {
        val saldoAtual = obterSaldo()

        if (saldoAtual.saldoDisponivel < valor) {
            Log.e("CarteiraLocal", "Saldo insuficiente: Tentou debitar $valor mas tem ${saldoAtual.saldoDisponivel}")
            return Result.failure(Exception("Saldo insuficiente"))
        }

        val novoSaldo = SaldoCarteira(
            saldoDisponivel = saldoAtual.saldoDisponivel - valor,
            saldoBloqueado = saldoAtual.saldoBloqueado,
            saldoTotal = saldoAtual.saldoTotal - valor
        )
        salvarSaldo(novoSaldo)
        Log.d("CarteiraLocal", "Saldo debitado: -$valor | Novo saldo: ${novoSaldo.saldoDisponivel}")
        return Result.success(novoSaldo)
    }

    fun bloquearSaldo(valor: Double): Result<SaldoCarteira> {
        val saldoAtual = obterSaldo()

        if (saldoAtual.saldoDisponivel < valor) {
            return Result.failure(Exception("Saldo insuficiente para bloquear"))
        }

        val novoSaldo = SaldoCarteira(
            saldoDisponivel = saldoAtual.saldoDisponivel - valor,
            saldoBloqueado = saldoAtual.saldoBloqueado + valor,
            saldoTotal = saldoAtual.saldoTotal
        )
        salvarSaldo(novoSaldo)
        Log.d("CarteiraLocal", "Saldo bloqueado: $valor")
        return Result.success(novoSaldo)
    }

    fun desbloquearSaldo(valor: Double): SaldoCarteira {
        val saldoAtual = obterSaldo()
        val novoSaldo = SaldoCarteira(
            saldoDisponivel = saldoAtual.saldoDisponivel + valor,
            saldoBloqueado = maxOf(0.0, saldoAtual.saldoBloqueado - valor),
            saldoTotal = saldoAtual.saldoTotal
        )
        salvarSaldo(novoSaldo)
        Log.d("CarteiraLocal", "Saldo desbloqueado: $valor")
        return novoSaldo
    }

    // ==================== TRANSAÇÕES ====================

    fun salvarTransacao(transacao: TransacaoCarteira) {
        val transacoes = obterTransacoes().toMutableList()

        // Remove transação antiga com mesmo ID (atualização)
        transacoes.removeAll { it.id == transacao.id }

        // Adiciona nova transação no início
        transacoes.add(0, transacao)

        // Limita a 100 transações
        if (transacoes.size > 100) {
            transacoes.subList(100, transacoes.size).clear()
        }

        val json = gson.toJson(transacoes)
        prefs.edit().putString(KEY_TRANSACOES, json).apply()

        Log.d("CarteiraLocal", "Transação salva: ${transacao.tipo} - R$ ${transacao.valor} - Status: ${transacao.status}")
    }

    fun obterTransacoes(): List<TransacaoCarteira> {
        val json = prefs.getString(KEY_TRANSACOES, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<TransacaoCarteira>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e("CarteiraLocal", "Erro ao carregar transações", e)
            emptyList()
        }
    }

    fun atualizarStatusTransacao(idTransacao: String, novoStatus: StatusTransacao) {
        val transacoes = obterTransacoes().toMutableList()
        val index = transacoes.indexOfFirst { it.id == idTransacao }

        if (index != -1) {
            val transacaoAtualizada = transacoes[index].copy(status = novoStatus)
            transacoes[index] = transacaoAtualizada

            val json = gson.toJson(transacoes)
            prefs.edit().putString(KEY_TRANSACOES, json).apply()

            Log.d("CarteiraLocal", "Status da transação $idTransacao atualizado para $novoStatus")
        }
    }

    fun obterTransacaoPorId(id: String): TransacaoCarteira? {
        return obterTransacoes().find { it.id == id }
    }

    // ==================== UTILITÁRIOS ====================

    fun limparDados() {
        prefs.edit().clear().apply()
        Log.d("CarteiraLocal", "Todos os dados da carteira foram limpos")
    }

    fun obterUltimaAtualizacao(): Long {
        return prefs.getString(KEY_ULTIMA_ATUALIZACAO, "0")?.toLongOrNull() ?: 0L
    }

    fun exportarDados(): Map<String, Any> {
        val saldo = obterSaldo()
        val transacoes = obterTransacoes()
        return mapOf(
            "saldo" to saldo,
            "transacoes" to transacoes,
            "ultima_atualizacao" to obterUltimaAtualizacao()
        )
    }

    // ==================== HELPERS ====================

    private fun gerarDataAtual(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
        return sdf.format(Date())
    }

    fun criarTransacaoDeposito(
        valor: Double,
        metodo: MetodoPagamento,
        referenciaPagBank: String? = null
    ): TransacaoCarteira {
        return TransacaoCarteira(
            id = "DEP_${System.currentTimeMillis()}",
            tipo = TipoTransacao.DEPOSITO,
            valor = valor,
            descricao = "Depósito via ${metodo.name}",
            data = gerarDataAtual(),
            status = StatusTransacao.PENDENTE,
            metodo = metodo,
            referenciaPagBank = referenciaPagBank
        )
    }

    fun criarTransacaoDebito(
        valor: Double,
        descricao: String,
        servicoId: String? = null
    ): TransacaoCarteira {
        return TransacaoCarteira(
            id = "DEB_${System.currentTimeMillis()}",
            tipo = TipoTransacao.PAGAMENTO_SERVICO,
            valor = valor,
            descricao = descricao,
            data = gerarDataAtual(),
            status = StatusTransacao.CONCLUIDO,
            metodo = MetodoPagamento.SALDO_CARTEIRA,
            referenciaPagBank = servicoId
        )
    }

    fun criarTransacaoEstorno(
        valor: Double,
        transacaoOriginalId: String
    ): TransacaoCarteira {
        return TransacaoCarteira(
            id = "EST_${System.currentTimeMillis()}",
            tipo = TipoTransacao.ESTORNO,
            valor = valor,
            descricao = "Estorno da transação $transacaoOriginalId",
            data = gerarDataAtual(),
            status = StatusTransacao.CONCLUIDO,
            metodo = null,
            referenciaPagBank = transacaoOriginalId
        )
    }
}

