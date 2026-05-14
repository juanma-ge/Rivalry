package com.example.rivalry.domain.model

data class EquipoClasificacion(
    val nombre: String,
    var puntos: Int = 0,
    var jugados: Int = 0,
    var victorias: Int = 0,
    var empates: Int = 0,
    var derrotas: Int = 0,
    var golesFavor: Int = 0,
    var golesContra: Int = 0
) {
    val diferenciaGoles: Int get() = golesFavor - golesContra
}