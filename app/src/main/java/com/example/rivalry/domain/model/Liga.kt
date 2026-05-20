package com.example.rivalry.domain.model

data class Liga(
    val id: String = "",
    val nombre: String = "",
    val deporte: String = Deporte.FUTBOL_7.name,
    val imagenLiga: String = "",
    val creadorId: String = "",
    val descripcion: String = "",
    val idsMiembros: List<String> = emptyList(),
    val maxParticipantes: Int = 20,
    val esPublica: Boolean = true,
    val provincia: String = "",
    val ciudad: String = "",
    val idsAgentesLibres: List<String> = emptyList(),
    val nombresEquipos: Map<String, String> = emptyMap(),
    val estado: String = "INSCRIPCION",
    val fotoUrl: String = ""
)
