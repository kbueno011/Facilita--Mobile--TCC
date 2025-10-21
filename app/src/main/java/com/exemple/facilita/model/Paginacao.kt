package com.exemple.facilita.model

data class Paginacao(
    val pagina_atual: Int,
    val total_paginas: Int,
    val total_pedidos: Int,
    val por_pagina: Int
)