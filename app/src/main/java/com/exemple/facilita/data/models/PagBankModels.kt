package com.exemple.facilita.data.models

import com.google.gson.annotations.SerializedName

// Modelos para integração com PagBank

data class PagBankCharge(
    @SerializedName("reference_id")
    val referenceId: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("amount")
    val amount: PagBankAmount,
    @SerializedName("payment_method")
    val paymentMethod: PagBankPaymentMethod,
    @SerializedName("notification_urls")
    val notificationUrls: List<String>? = null
)

data class PagBankAmount(
    @SerializedName("value")
    val value: Int, // Valor em centavos
    @SerializedName("currency")
    val currency: String = "BRL"
)

data class PagBankPaymentMethod(
    @SerializedName("type")
    val type: String, // CREDIT_CARD, DEBIT_CARD, PIX, BOLETO
    @SerializedName("installments")
    val installments: Int = 1,
    @SerializedName("capture")
    val capture: Boolean = true,
    @SerializedName("card")
    val card: PagBankCard? = null,
    @SerializedName("pix")
    val pix: PagBankPix? = null
)

data class PagBankCard(
    @SerializedName("number")
    val number: String,
    @SerializedName("exp_month")
    val expMonth: String,
    @SerializedName("exp_year")
    val expYear: String,
    @SerializedName("security_code")
    val securityCode: String,
    @SerializedName("holder")
    val holder: PagBankCardHolder
)

data class PagBankCardHolder(
    @SerializedName("name")
    val name: String
)

data class PagBankPix(
    @SerializedName("expiration_date")
    val expirationDate: String? = null
)

data class PagBankChargeResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("reference_id")
    val referenceId: String,
    @SerializedName("status")
    val status: String, // AUTHORIZED, PAID, DECLINED, CANCELED
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("amount")
    val amount: PagBankAmount,
    @SerializedName("payment_method")
    val paymentMethod: PagBankPaymentMethodResponse?,
    @SerializedName("links")
    val links: List<PagBankLink>?
)

data class PagBankPaymentMethodResponse(
    @SerializedName("type")
    val type: String,
    @SerializedName("pix")
    val pix: PagBankPixResponse? = null
)

data class PagBankPixResponse(
    @SerializedName("qr_code")
    val qrCode: String,
    @SerializedName("qr_code_base64")
    val qrCodeBase64: String,
    @SerializedName("expiration_date")
    val expirationDate: String
)

data class PagBankLink(
    @SerializedName("rel")
    val rel: String,
    @SerializedName("href")
    val href: String,
    @SerializedName("media")
    val media: String,
    @SerializedName("type")
    val type: String
)

// Modelos locais para a carteira
data class TransacaoCarteira(
    val id: String,
    val tipo: TipoTransacao,
    val valor: Double,
    val descricao: String,
    val data: String,
    val status: StatusTransacao,
    val metodo: MetodoPagamento?,
    val referenciaPagBank: String? = null
)

enum class TipoTransacao {
    DEPOSITO,
    SAQUE,
    PAGAMENTO_SERVICO,
    RECEBIMENTO,
    CASHBACK,
    ESTORNO
}

enum class StatusTransacao {
    PENDENTE,
    PROCESSANDO,
    CONCLUIDO,
    FALHOU,
    CANCELADO
}

enum class MetodoPagamento {
    PIX,
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    BOLETO,
    SALDO_CARTEIRA
}

data class SaldoCarteira(
    val saldoDisponivel: Double,
    val saldoBloqueado: Double,
    val saldoTotal: Double
)

data class CartaoSalvo(
    val id: String,
    val ultimos4Digitos: String,
    val bandeira: String,
    val nomeCompleto: String,
    val validade: String,
    val isPrincipal: Boolean = false
)

data class ContaBancaria(
    val id: String,
    val banco: String,
    val agencia: String,
    val conta: String,
    val tipoConta: String, // CORRENTE ou POUPANCA
    val nomeCompleto: String,
    val cpf: String,
    val isPrincipal: Boolean = false
)

