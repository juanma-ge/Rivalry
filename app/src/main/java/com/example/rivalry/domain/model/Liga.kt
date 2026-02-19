package com.example.rivalry.domain.model

data class Liga(
    val id: String = "",
    val nombre: String = "",
    val imagenLiga: String = "",
    val creadorId: String = "",
    val descripcion: String = "",
    val idsMiembros: List<String> = emptyList()
)
