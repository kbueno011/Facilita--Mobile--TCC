package com.exemple.facilita.data.models

import com.google.gson.annotations.SerializedName

// Modelo de Notificação
data class Notificacao(
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_usuario")
    val idUsuario: Int,
    @SerializedName("tipo")
    val tipo: String, // "SERVICO_ACEITO", "SERVICO_INICIADO", "SERVICO_CONCLUIDO", "MENSAGEM"
    @SerializedName("titulo")
    val titulo: String,
    @SerializedName("mensagem")
    val mensagem: String,
    @SerializedName("data")
    val data: String,
    @SerializedName("lida")
    val lida: Boolean,
    @SerializedName("id_servico")
    val idServico: Int?,
    @SerializedName("dados_extras")
    val dadosExtras: String?
)

// Response de notificações
data class NotificacoesResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("data")
    val data: List<Notificacao>?
)

// Response de marcar como lida
data class MarcarLidaResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("message")
    val message: String?
)

// Tipos de notificação
enum class TipoNotificacao {
    SERVICO_ACEITO,
    SERVICO_INICIADO,
    SERVICO_CONCLUIDO,
    SERVICO_CANCELADO,
    MENSAGEM,
    PAGAMENTO,
    SISTEMA;

    companion object {
        fun fromString(tipo: String): TipoNotificacao {
            return when(tipo.uppercase()) {
                "SERVICO_ACEITO" -> SERVICO_ACEITO
                "SERVICO_INICIADO" -> SERVICO_INICIADO
                "SERVICO_CONCLUIDO" -> SERVICO_CONCLUIDO
                "SERVICO_CANCELADO" -> SERVICO_CANCELADO
                "MENSAGEM" -> MENSAGEM
                "PAGAMENTO" -> PAGAMENTO
                else -> SISTEMA
            }
        }
    }
}

