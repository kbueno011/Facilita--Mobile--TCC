package com.exemple.facilita.model
import java.io.Serializable

data class ServicoData(
    val id: Int,
    val id_contratante: Int,
    val id_prestador: Int?,
    val id_categoria: Int,
    val descricao: String,
    val status: String,
    val data_solicitacao: String,
    val data_conclusao: String?,
    val data_confirmacao: String?,
    val id_localizacao: Int?,
    val valor: String,
    val tempo_estimado: Int?,
    val data_inicio: String?,
    val contratante: Contratante?,
    val prestador: Any?,
    val categoria: Categoria?,
    val localizacao: Any?,
    val pagamentos: List<Any>?,
    val detalhes_valor: DetalhesValor?
): Serializable