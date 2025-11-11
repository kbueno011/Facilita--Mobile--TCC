package com.exemple.facilita.data.api

import com.exemple.facilita.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface PagBankApiService {

    @POST("charges")
    suspend fun criarCobranca(
        @Body charge: PagBankCharge
    ): Response<PagBankChargeResponse>

    @GET("charges/{id}")
    suspend fun consultarCobranca(
        @Path("id") chargeId: String
    ): Response<PagBankChargeResponse>

    @POST("charges/{id}/cancel")
    suspend fun cancelarCobranca(
        @Path("id") chargeId: String
    ): Response<PagBankChargeResponse>
}

// API local para gerenciar carteira
interface CarteiraApiService {

    @GET("carteira/saldo")
    suspend fun obterSaldo(
        @Header("Authorization") token: String
    ): Response<SaldoCarteira>

    @POST("carteira/deposito")
    suspend fun realizarDeposito(
        @Header("Authorization") token: String,
        @Body request: DepositoRequest
    ): Response<TransacaoCarteira>

    @POST("carteira/saque")
    suspend fun realizarSaque(
        @Header("Authorization") token: String,
        @Body request: SaqueRequest
    ): Response<TransacaoCarteira>

    @GET("carteira/transacoes")
    suspend fun obterTransacoes(
        @Header("Authorization") token: String,
        @Query("pagina") pagina: Int = 1,
        @Query("limite") limite: Int = 20
    ): Response<List<TransacaoCarteira>>

    @POST("carteira/cartao")
    suspend fun adicionarCartao(
        @Header("Authorization") token: String,
        @Body request: CartaoRequest
    ): Response<CartaoSalvo>

    @GET("carteira/cartoes")
    suspend fun obterCartoes(
        @Header("Authorization") token: String
    ): Response<List<CartaoSalvo>>

    @DELETE("carteira/cartao/{id}")
    suspend fun removerCartao(
        @Header("Authorization") token: String,
        @Path("id") cartaoId: String
    ): Response<Unit>

    @POST("carteira/conta-bancaria")
    suspend fun adicionarContaBancaria(
        @Header("Authorization") token: String,
        @Body request: ContaBancariaRequest
    ): Response<ContaBancaria>

    @GET("carteira/contas-bancarias")
    suspend fun obterContasBancarias(
        @Header("Authorization") token: String
    ): Response<List<ContaBancaria>>

    @DELETE("carteira/conta-bancaria/{id}")
    suspend fun removerContaBancaria(
        @Header("Authorization") token: String,
        @Path("id") contaId: String
    ): Response<Unit>
}

// Request models
data class DepositoRequest(
    val valor: Double,
    val metodoPagamento: String, // PIX, CARTAO_CREDITO, CARTAO_DEBITO, BOLETO
    val cartaoId: String? = null,
    val parcelamento: Int = 1
)

data class SaqueRequest(
    val valor: Double,
    val contaBancariaId: String
)

data class CartaoRequest(
    val numero: String,
    val nomeCompleto: String,
    val validade: String,
    val cvv: String,
    val isPrincipal: Boolean = false
)

data class ContaBancariaRequest(
    val banco: String,
    val agencia: String,
    val conta: String,
    val tipoConta: String,
    val nomeCompleto: String,
    val cpf: String,
    val isPrincipal: Boolean = false
)

