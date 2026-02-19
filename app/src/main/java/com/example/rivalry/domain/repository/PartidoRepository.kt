package com.example.rivalry.domain.repository

import com.example.rivalry.domain.model.Partido

interface PartidoRepository {

    suspend fun crearPartido(partido: Partido): Boolean
    suspend fun obtenerPartidoLiga(idLiga: String): List<Partido>

}