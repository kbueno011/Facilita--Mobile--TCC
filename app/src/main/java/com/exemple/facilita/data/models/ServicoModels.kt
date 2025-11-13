package com.exemple.facilita.data.models

import com.google.gson.annotations.SerializedName

// Modelo de Serviço (conforme API real)
data class Servico(
    @SerializedName("id")
    val id: Int,
    @SerializedName("id_contratante")
    val idContratante: Int,
    @SerializedName("id_prestador")
    val idPrestador: Int?,
    @SerializedName("id_categoria")
    val idCategoria: Int,
    @SerializedName("descricao")
    val descricao: String?,
    @SerializedName("status")
    val status: String, // "AGUARDANDO", "ACEITO", "EM_ANDAMENTO", "CONCLUIDO"
    @SerializedName("data_solicitacao")
    val dataSolicitacao: String?,
    @SerializedName("data_conclusao")
    val dataConclusao: String?,
    @SerializedName("data_confirmacao")
    val dataConfirmacao: String?,
    @SerializedName("valor")
    val valor: String,
    @SerializedName("tempo_estimado")
    val tempoEstimado: Int?,
    @SerializedName("data_inicio")
    val dataInicio: String?,
    @SerializedName("contratante")
    val contratante: Contratante?,
    @SerializedName("prestador")
    val prestador: PrestadorInfo?,
    @SerializedName("categoria")
    val categoria: Categoria?,
    @SerializedName("localizacao")
    val localizacao: Localizacao?
)

// Status do serviço na API
enum class StatusServicoApi {
    @SerializedName("AGUARDANDO")
    AGUARDANDO,

    @SerializedName("ACEITO")
    ACEITO,

    @SerializedName("EM_ANDAMENTO")
    EM_ANDAMENTO,

    @SerializedName("CONCLUIDO")
    CONCLUIDO,

    @SerializedName("CANCELADO")
    CANCELADO;

    companion object {
        fun fromString(status: String): StatusServicoApi {
            return when(status.uppercase()) {
                "AGUARDANDO" -> AGUARDANDO
                "ACEITO" -> ACEITO
                "EM_ANDAMENTO" -> EM_ANDAMENTO
                "CONCLUIDO" -> CONCLUIDO
                "CANCELADO" -> CANCELADO
                else -> AGUARDANDO
            }
        }
    }
}

// Contratante
data class Contratante(
    @SerializedName("id")
    val id: Int,
    @SerializedName("necessidade")
    val necessidade: String?,
    @SerializedName("id_usuario")
    val idUsuario: Int,
    @SerializedName("cpf")
    val cpf: String,
    @SerializedName("usuario")
    val usuario: Usuario?
)

// Usuário
data class Usuario(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nome")
    val nome: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("telefone")
    val telefone: String?,
    @SerializedName("foto_perfil")
    val fotoPerfil: String?,
    @SerializedName("tipo_conta")
    val tipoConta: String
)

// Informações do prestador
data class PrestadorInfo(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nome")
    val nome: String,
    @SerializedName("foto_url")
    val fotoUrl: String?,
    @SerializedName("avaliacao")
    val avaliacao: Float = 5.0f,
    @SerializedName("telefone")
    val telefone: String?,
    @SerializedName("veiculo")
    val veiculo: VeiculoInfo?,
    @SerializedName("latitude_atual")
    val latitudeAtual: Double?,
    @SerializedName("longitude_atual")
    val longitudeAtual: Double?,
    @SerializedName("usuario")
    val usuario: Usuario?
)

// Categoria
data class Categoria(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nome")
    val nome: String,
    @SerializedName("descricao")
    val descricao: String?,
    @SerializedName("icone")
    val icone: String?,
    @SerializedName("preco_base")
    val precoBase: String,
    @SerializedName("tempo_medio")
    val tempoMedio: Int?
)

// Localização
data class Localizacao(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("latitude")
    val latitude: Double?,
    @SerializedName("longitude")
    val longitude: Double?,
    @SerializedName("endereco")
    val endereco: String?,
    @SerializedName("numero")
    val numero: String?,
    @SerializedName("complemento")
    val complemento: String?,
    @SerializedName("bairro")
    val bairro: String?,
    @SerializedName("cidade")
    val cidade: String?,
    @SerializedName("estado")
    val estado: String?,
    @SerializedName("cep")
    val cep: String?
)

// Informações do veículo
data class VeiculoInfo(
    @SerializedName("marca")
    val marca: String,
    @SerializedName("modelo")
    val modelo: String,
    @SerializedName("placa")
    val placa: String,
    @SerializedName("cor")
    val cor: String,
    @SerializedName("ano")
    val ano: String?
)

// Response de meus serviços
data class MeusServicosResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("data")
    val data: List<Servico>?
)

// Response de um serviço específico
data class ServicoResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("data")
    val data: Servico?,
    @SerializedName("message")
    val message: String?
)

// Response de busca por status (com paginação)
data class ServicosPorStatusResponse(
    @SerializedName("status_code")
    val statusCode: Int,
    @SerializedName("data")
    val data: ServicosPorStatusData?
)

data class ServicosPorStatusData(
    @SerializedName("pedidos")
    val pedidos: List<ServicoPedido>?,
    @SerializedName("paginacao")
    val paginacao: Paginacao?
)

data class ServicoPedido(
    @SerializedName("id")
    val id: Int,
    @SerializedName("descricao")
    val descricao: String?,
    @SerializedName("status")
    val status: String,
    @SerializedName("valor")
    val valor: Double,
    @SerializedName("data_solicitacao")
    val dataSolicitacao: String?,
    @SerializedName("data_conclusao")
    val dataConclusao: String?,
    @SerializedName("categoria")
    val categoria: Categoria?,
    @SerializedName("localizacao")
    val localizacao: Localizacao?,
    @SerializedName("prestador")
    val prestador: PrestadorInfo?
)

data class Paginacao(
    @SerializedName("pagina_atual")
    val paginaAtual: Int,
    @SerializedName("total_paginas")
    val totalPaginas: Int,
    @SerializedName("total_pedidos")
    val totalPedidos: Int,
    @SerializedName("por_pagina")
    val porPagina: Int
)


