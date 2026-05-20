package com.example.rivalry.domain.model

data class Usuario(
    val id: String = "",
    val email: String = "",
    val nombreUsuario: String = "",
    val avatarUrl: String = "",
    val ligasJugadas: List<String> = emptyList(),
    val esAdmin: Boolean = false,
    val amigos: List<String> = emptyList()
)
