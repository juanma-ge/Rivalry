package com.example.rivalry.domain.model

data class Mensaje(
    val id: String = "",
    val salaId: String = "",
    val remitenteId: String = "",
    val remitenteNombre: String = "",
    val texto: String = "",
    val timestamp: Long = System.currentTimeMillis()
)