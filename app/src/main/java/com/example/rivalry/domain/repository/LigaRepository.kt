package com.example.rivalry.domain.repository

import com.example.rivalry.domain.model.Liga

interface LigaRepository {

    suspend fun crearLiga(liga: Liga): Boolean
    suspend fun obtenerLigasDelUsuario(idUsuario: String): List<Liga>
}