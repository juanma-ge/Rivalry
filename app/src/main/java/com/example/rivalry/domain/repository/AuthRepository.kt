package com.example.rivalry.domain.repository

interface AuthRepository {

    suspend fun login(email: String, contrasenia: String): String?

    suspend fun registro(email: String, contrasenia: String): String?

    fun obtenerIdUsuarioActual(): String?

    fun cerrarSesion()

}