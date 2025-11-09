package com.exemple.facilita.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Tipos de notificação suportados pelo sistema
 */
enum class TipoNotificacao {
    PEDIDO_ACEITO,
    PEDIDO_RECUSADO,
    PEDIDO_EM_ANDAMENTO,
    PEDIDO_CONCLUIDO,
    PEDIDO_CANCELADO,
    PRESTADOR_CHEGOU,
    PRESTADOR_A_CAMINHO,
    PAGAMENTO_APROVADO,
    PAGAMENTO_RECUSADO,
    SALDO_RECEBIDO,
    NOVO_CUPOM,
    PROMOCAO,
    AVALIACAO_RECEBIDA,
    MENSAGEM_SISTEMA,
    ATUALIZACAO_APP
}

/**
 * Prioridade da notificação
 */
enum class PrioridadeNotificacao {
    BAIXA,
    MEDIA,
    ALTA,
    URGENTE
}

/**
 * Status de leitura da notificação
 */
enum class StatusNotificacao {
    NAO_LIDA,
    LIDA,
    ARQUIVADA
}

/**
 * Modelo de dados para notificações
 */
data class Notificacao(
    val id: String,
    val tipo: TipoNotificacao,
    val titulo: String,
    val mensagem: String,
    val dataHora: LocalDateTime = LocalDateTime.now(),
    val prioridade: PrioridadeNotificacao = PrioridadeNotificacao.MEDIA,
    val status: StatusNotificacao = StatusNotificacao.NAO_LIDA,
    val icone: ImageVector? = null,
    val corFundo: Long? = null,
    val acaoPrincipal: AcaoNotificacao? = null,
    val acaoSecundaria: AcaoNotificacao? = null,
    val dadosExtras: Map<String, String> = emptyMap()
) {
    /**
     * Retorna o ícone apropriado baseado no tipo de notificação
     */
    fun obterIcone(): ImageVector {
        return icone ?: when (tipo) {
            TipoNotificacao.PEDIDO_ACEITO -> Icons.Default.CheckCircle
            TipoNotificacao.PEDIDO_RECUSADO -> Icons.Default.Cancel
            TipoNotificacao.PEDIDO_EM_ANDAMENTO -> Icons.Default.LocalShipping
            TipoNotificacao.PEDIDO_CONCLUIDO -> Icons.Default.Done
            TipoNotificacao.PEDIDO_CANCELADO -> Icons.Default.Close
            TipoNotificacao.PRESTADOR_CHEGOU -> Icons.Default.LocationOn
            TipoNotificacao.PRESTADOR_A_CAMINHO -> Icons.Default.DirectionsCar
            TipoNotificacao.PAGAMENTO_APROVADO -> Icons.Default.Payment
            TipoNotificacao.PAGAMENTO_RECUSADO -> Icons.Default.Warning
            TipoNotificacao.SALDO_RECEBIDO -> Icons.Default.AccountBalanceWallet
            TipoNotificacao.NOVO_CUPOM -> Icons.Default.LocalOffer
            TipoNotificacao.PROMOCAO -> Icons.Default.Star
            TipoNotificacao.AVALIACAO_RECEBIDA -> Icons.Default.StarRate
            TipoNotificacao.MENSAGEM_SISTEMA -> Icons.Default.Notifications
            TipoNotificacao.ATUALIZACAO_APP -> Icons.Default.Update
        }
    }

    /**
     * Retorna a cor de fundo apropriada baseada no tipo
     */
    fun obterCorFundo(): Long {
        return corFundo ?: when (tipo) {
            TipoNotificacao.PEDIDO_ACEITO -> 0xFF4CAF50
            TipoNotificacao.PEDIDO_RECUSADO -> 0xFFF44336
            TipoNotificacao.PEDIDO_EM_ANDAMENTO -> 0xFF2196F3
            TipoNotificacao.PEDIDO_CONCLUIDO -> 0xFF4CAF50
            TipoNotificacao.PEDIDO_CANCELADO -> 0xFF9E9E9E
            TipoNotificacao.PRESTADOR_CHEGOU -> 0xFFFF9800
            TipoNotificacao.PRESTADOR_A_CAMINHO -> 0xFF2196F3
            TipoNotificacao.PAGAMENTO_APROVADO -> 0xFF4CAF50
            TipoNotificacao.PAGAMENTO_RECUSADO -> 0xFFF44336
            TipoNotificacao.SALDO_RECEBIDO -> 0xFF4CAF50
            TipoNotificacao.NOVO_CUPOM -> 0xFFFF9800
            TipoNotificacao.PROMOCAO -> 0xFFFFEB3B
            TipoNotificacao.AVALIACAO_RECEBIDA -> 0xFFFFEB3B
            TipoNotificacao.MENSAGEM_SISTEMA -> 0xFF9C27B0
            TipoNotificacao.ATUALIZACAO_APP -> 0xFF2196F3
        }
    }

    /**
     * Formata a data/hora da notificação de forma amigável
     */
    fun obterTempoDecorrido(): String {
        val agora = LocalDateTime.now()
        val diferenca = java.time.Duration.between(dataHora, agora)

        return when {
            diferenca.toMinutes() < 1 -> "Agora"
            diferenca.toMinutes() < 60 -> "${diferenca.toMinutes()}m atrás"
            diferenca.toHours() < 24 -> "${diferenca.toHours()}h atrás"
            diferenca.toDays() < 7 -> "${diferenca.toDays()}d atrás"
            else -> dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    }
}

/**
 * Ação que pode ser executada a partir de uma notificação
 */
data class AcaoNotificacao(
    val texto: String,
    val rota: String? = null,
    val callback: (() -> Unit)? = null
)

/**
 * Resposta da API para notificações
 */
data class NotificacaoResponse(
    val notificacoes: List<Notificacao>,
    val totalNaoLidas: Int,
    val total: Int
)

