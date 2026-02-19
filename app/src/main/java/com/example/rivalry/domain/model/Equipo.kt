package com.example.rivalry.domain.model

data class Equipo(
    val id: String = "",
    val nombre: String = "",
    val idCreador: String = "",
    val idLiga: String = "",
    val puntos: Int = 0,
    val fotoEquipo: String = "",
    val jugadores: List<Jugador> = emptyList()
)
