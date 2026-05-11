package com.example.rivalry.domain.model

data class Partido(
    val id: String = "",
    val idLiga: String = "",
    val idLocal: String = "",
    val idVisitante: String = "",
    val resultado: String = "",
    val terminado: Boolean = false,
    val dateTimestamp: Long = 0L,
    val prediccionTiempo: String = "",
    val probabilidadVictoriaLocal: String = "",
    val provincia: String = "",
    val ciudad: String = "",
    val ubicacion: String = "",
    val jornada: Int = 1,
    val nombreLocal: String = "",
    val nombreVisitante: String = "",
    val golesLocal: Int = 0,
    val golesVisitante: Int = 0,
    val estado: String = "PENDIENTE", // Puede ser: "PENDIENTE", "FINALIZADO"
    val goleadores: Map<String, Int> = emptyMap()
)
