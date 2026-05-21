package com.example.rivalry.domain.model

data class MensajePrivado(
    val id: String = "",
    val idEmisor: String = "",
    val texto: String = "",
    val fecha: Long = 0L
)