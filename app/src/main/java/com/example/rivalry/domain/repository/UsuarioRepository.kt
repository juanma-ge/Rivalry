package com.example.rivalry.domain.repository

import com.example.rivalry.domain.model.Usuario

interface UsuarioRepository {

    suspend fun guardarUsuario(usuario: Usuario): Boolean
    suspend fun obtenerUsuario(id: String): Usuario?

}