package com.example.rivalry.domain.model

data class PartidoSuelto(
    val id: String = "",
    val deporte: String = "",
    val creadorId: String = "",
    val idsJugadores: List<String> = emptyList(),
    val maxJugadores: Int = 0,
    val fecha: String = "",
    val hora: String = "",
    val ubicacion: String = "",
    val nivel: String = "Amateur",
    val provincia: String = "",
    val ciudad: String = ""
)