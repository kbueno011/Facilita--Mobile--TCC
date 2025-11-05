package com.exemple.facilita.model

data class ServicoCategoriaRequest(
    val descricao_personalizada: String,
    val valor_adicional: Double,
    val origem_lat: Double,
    val origem_lng: Double,
    val origem_endereco: String,
    val destino_lat: Double,
    val destino_lng: Double,
    val destino_endereco: String,
    val paradas: List<ParadaServico>?
)

data class ParadaServico(
    val lat: Double,
    val lng: Double,
    val descricao: String,
    val endereco_completo: String
)

data class ServicoCategoriaResponse(
    val status_code: Int,
    val message: String,
    val data: ServicoCategoriaData
)

data class ServicoCategoriaData(
    val servico: ServicoDetalhado,
    val categoria: CategoriaDetalhada,
    val detalhes_calculo: DetalhesCalculo
)

data class ServicoDetalhado(
    val id: Int,
    val id_contratante: Int,
    val id_prestador: Int?,
    val id_categoria: Int,
    val descricao: String,
    val status: String,
    val data_solicitacao: String,
    val valor: String
)

data class CategoriaDetalhada(
    val nome: String,
    val descricao: String,
    val icone: String,
    val preco_base: String,
    val tempo_medio: Int
)


