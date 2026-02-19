package com.example.rivalry.domain.repository

import com.example.rivalry.domain.model.Equipo
import com.example.rivalry.domain.model.Liga

interface EquipoRepository {

    suspend fun crearEquipo(equipo: Equipo): Boolean
    suspend fun obtenerEquiposLiga(idLiga: Liga): List<Equipo>
    suspend fun actualizarEquipo(equipo: Equipo): Boolean

}