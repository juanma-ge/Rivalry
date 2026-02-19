package com.example.rivalry.domain.model

data class Jugador(
    val id: String = "",
    val nombre: String = "",
    val apodo: String = "",
    val posicion: Posicion = Posicion.MED,
    val dorsal: Int = 0,
    val partidosJugados: Int = 0,
    val goles: Int = 0,
    val asistencias: Int = 0,
    val tarjetasAmarillas: Int = 0,
    val expulsiones: Int = 0,
    val fotoJugador: String = ""
)
