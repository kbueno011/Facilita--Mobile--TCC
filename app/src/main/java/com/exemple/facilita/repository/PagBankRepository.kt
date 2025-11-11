package com.exemple.facilita.repository

import android.util.Log
import com.exemple.facilita.data.api.PagBankApiService
import com.exemple.facilita.data.models.*
import com.exemple.facilita.network.PagBankClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class PagBankRepository {

    private val api = PagBankClient.createService<PagBankApiService>()

    // Modo simulado para testes locais
    private val MODO_SIMULADO = true // Mude para false quando tiver o token real

    suspend fun criarCobrancaPix(
        referenceId: String,
        valor: Double,
        descricao: String
    ): Result<PagBankChargeResponse> = withContext(Dispatchers.IO) {
        try {
            // MODO SIMULADO - Para testar sem token real
            if (MODO_SIMULADO) {
                Log.d("PagBankRepository", "⚠️ MODO SIMULADO - Gerando QR Code fake")
                delay(1500) // Simula delay da API

                val qrCodeSimulado = "00020126330014br.gov.bcb.pix0111123456789015204000053039865802BR5913Facilita App6009SAO PAULO62070503***63041D3D"

                val responseSimulado = PagBankChargeResponse(
                    id = referenceId,
                    referenceId = referenceId,
                    status = "WAITING",
                    createdAt = System.currentTimeMillis().toString(),
                    amount = PagBankAmount(
                        value = (valor * 100).toInt(),
                        currency = "BRL"
                    ),
                    paymentMethod = PagBankPaymentMethodResponse(
                        type = "PIX",
                        pix = PagBankPixResponse(
                            qrCode = qrCodeSimulado,
                            qrCodeBase64 = gerarQrCodeBase64Simulado(),
                            expirationDate = calcularDataExpiracao(10)
                        )
                    ),
                    links = null
                )

                Log.d("PagBankRepository", "✅ QR Code simulado gerado com sucesso")
                return@withContext Result.success(responseSimulado)
            }

            // MODO REAL - Chama API do PagBank
            val charge = PagBankCharge(
                referenceId = referenceId,
                description = descricao,
                amount = PagBankAmount(
                    value = (valor * 100).toInt(),
                    currency = "BRL"
                ),
                paymentMethod = PagBankPaymentMethod(
                    type = "PIX",
                    pix = PagBankPix(
                        expirationDate = calcularDataExpiracao(10)
                    )
                )
            )

            Log.d("PagBankRepository", "Criando cobrança PIX real: $charge")

            val response = api.criarCobranca(charge)

            if (response.isSuccessful && response.body() != null) {
                Log.d("PagBankRepository", "Cobrança criada com sucesso: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                val error = "Erro ao criar cobrança: ${response.code()} - ${response.message()}"
                Log.e("PagBankRepository", error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e("PagBankRepository", "Erro ao criar cobrança PIX", e)
            Result.failure(e)
        }
    }

    private fun gerarQrCodeBase64Simulado(): String {
        // QR Code simulado em Base64 (imagem pequena de exemplo)
        return "iVBORw0KGgoAAAANSUhEUgAAAAoAAAAKCAYAAACNMs+9AAAAFUlEQVR42mNk+M9Qz0AEYBxVSF+FABJADveWkH6oAAAAAElFTkSuQmCC"
    }

    suspend fun criarCobrancaCartao(
        referenceId: String,
        valor: Double,
        descricao: String,
        numeroCartao: String,
        mesExpiracao: String,
        anoExpiracao: String,
        cvv: String,
        nomeCompleto: String,
        parcelamento: Int = 1
    ): Result<PagBankChargeResponse> = withContext(Dispatchers.IO) {
        try {
            // MODO SIMULADO - Para testar sem token real
            if (MODO_SIMULADO) {
                Log.d("PagBankRepository", "⚠️ MODO SIMULADO - Processando cartão fake")
                delay(2000) // Simula delay da API

                // Simula aprovação/recusa baseado no número do cartão
                val aprovado = numeroCartao.endsWith("1111") // Cartão 4111111111111111 aprova

                val responseSimulado = PagBankChargeResponse(
                    id = referenceId,
                    referenceId = referenceId,
                    status = if (aprovado) "PAID" else "DECLINED",
                    createdAt = System.currentTimeMillis().toString(),
                    amount = PagBankAmount(
                        value = (valor * 100).toInt(),
                        currency = "BRL"
                    ),
                    paymentMethod = PagBankPaymentMethodResponse(
                        type = "CREDIT_CARD",
                        pix = null
                    ),
                    links = null
                )

                if (aprovado) {
                    Log.d("PagBankRepository", "✅ Cartão simulado aprovado")
                } else {
                    Log.d("PagBankRepository", "❌ Cartão simulado recusado")
                }

                return@withContext Result.success(responseSimulado)
            }

            // MODO REAL - Chama API do PagBank
            val charge = PagBankCharge(
                referenceId = referenceId,
                description = descricao,
                amount = PagBankAmount(
                    value = (valor * 100).toInt(),
                    currency = "BRL"
                ),
                paymentMethod = PagBankPaymentMethod(
                    type = "CREDIT_CARD",
                    installments = parcelamento,
                    capture = true,
                    card = PagBankCard(
                        number = numeroCartao.replace(" ", ""),
                        expMonth = mesExpiracao,
                        expYear = anoExpiracao,
                        securityCode = cvv,
                        holder = PagBankCardHolder(name = nomeCompleto)
                    )
                )
            )

            Log.d("PagBankRepository", "Criando cobrança cartão")

            val response = api.criarCobranca(charge)

            if (response.isSuccessful && response.body() != null) {
                Log.d("PagBankRepository", "Cobrança cartão criada: ${response.body()}")
                Result.success(response.body()!!)
            } else {
                val error = "Erro ao criar cobrança cartão: ${response.code()}"
                Log.e("PagBankRepository", error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e("PagBankRepository", "Erro ao criar cobrança cartão", e)
            Result.failure(e)
        }
    }

    suspend fun consultarCobranca(chargeId: String): Result<PagBankChargeResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.consultarCobranca(chargeId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao consultar cobrança"))
                }
            } catch (e: Exception) {
                Log.e("PagBankRepository", "Erro ao consultar cobrança", e)
                Result.failure(e)
            }
        }

    suspend fun cancelarCobranca(chargeId: String): Result<PagBankChargeResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = api.cancelarCobranca(chargeId)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Erro ao cancelar cobrança"))
                }
            } catch (e: Exception) {
                Log.e("PagBankRepository", "Erro ao cancelar cobrança", e)
                Result.failure(e)
            }
        }

    private fun calcularDataExpiracao(minutos: Int): String {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.MINUTE, minutos)

        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
        dateFormat.timeZone = java.util.TimeZone.getTimeZone("America/Sao_Paulo")

        return dateFormat.format(calendar.time)
    }
}

