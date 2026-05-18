package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rivalry.domain.model.Deporte
import com.example.rivalry.domain.model.Goleador
import com.example.rivalry.domain.model.Liga
import com.example.rivalry.domain.model.MiembroUI
import com.example.rivalry.domain.model.Partido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FieldValue
import kotlin.collections.filter

class LigaViewModel : ViewModel() {

    private val _misLigas = MutableStateFlow<List<Liga>>(emptyList())
    val misLigas: StateFlow<List<Liga>> = _misLigas

    private val _ligasExplorar = MutableStateFlow<List<Liga>>(emptyList())
    val ligasExplorar: StateFlow<List<Liga>> = _ligasExplorar

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando

    init {
        cargarLigas()
    }

    private fun cargarLigas() {
        _cargando.value = true

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            _cargando.value = false
            return
        }

        FirebaseFirestore.getInstance().collection("ligas")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    _cargando.value = false
                    return@addSnapshotListener
                }

                val todasLasLigas = snapshot.documents.mapNotNull { documento ->
                    val liga = documento.toObject(Liga::class.java)
                    liga?.copy(id = documento.id)
                }
                _misLigas.value = todasLasLigas.filter { liga ->
                    liga.idsMiembros.contains(userId)
                }

                _ligasExplorar.value = todasLasLigas.filter { liga ->
                    liga.esPublica && !liga.idsMiembros.contains(userId)
                }

                _cargando.value = false
            }
    }

    fun crearLigaEnFirebase(
        nombre: String,
        deporte: Deporte,
        maxParticipantes: Int,
        esPublica: Boolean,
        provincia: String,
        ciudad: String,
        onExito: () -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true

            val creadorId = FirebaseAuth.getInstance().currentUser?.uid

            if (creadorId != null) {
                val nuevaLiga = Liga(
                    id = "",
                    nombre = nombre,
                    deporte = deporte.name,
                    creadorId = creadorId,
                    maxParticipantes = maxParticipantes,
                    esPublica = esPublica,
                    idsMiembros = listOf(creadorId),
                    provincia = provincia,
                    ciudad = ciudad
                )

                FirebaseFirestore.getInstance().collection("ligas")
                    .add(nuevaLiga)
                    .addOnSuccessListener { documento ->
                        FirebaseFirestore.getInstance().collection("ligas")
                            .document(documento.id)
                            .update("id", documento.id)

                        _cargando.value = false
                        onExito()
                    }
                    .addOnFailureListener {
                        _cargando.value = false
                    }
            }
        }
    }

    private val _ligaSeleccionada = MutableStateFlow<Liga?>(null)
    val ligaSeleccionada: StateFlow<Liga?> = _ligaSeleccionada.asStateFlow()

    private val _miembrosLiga = MutableStateFlow<List<MiembroUI>>(emptyList())
    val miembrosLiga: StateFlow<List<MiembroUI>> = _miembrosLiga.asStateFlow()

    fun cargarMiembros(ids: List<String>, creadorId: String, nombresEquipos: Map<String, String>) {
        if (ids.isEmpty()) {
            _miembrosLiga.value = emptyList()
            return
        }

        FirebaseFirestore.getInstance().collection("usuarios")
            .whereIn(com.google.firebase.firestore.FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { doc ->
                    val nombre = doc.getString("apodo")
                        ?: doc.getString("nombreUsuario")
                        ?: doc.getString("nombre")
                        ?: "Jugador"

                    val esAdmin = doc.id == creadorId
                    val nombreEquipo = nombresEquipos[doc.id] ?: "Equipo de $nombre"

                    MiembroUI(doc.id, nombre, esAdmin, nombreEquipo)
                }
                _miembrosLiga.value = lista
            }
            .addOnFailureListener {
                _miembrosLiga.value = emptyList()
            }
    }

    fun cargarDetalleLiga(ligaId: String) {
        _cargando.value = true

        FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    _cargando.value = false
                    return@addSnapshotListener
                }

                val liga = snapshot.toObject(Liga::class.java)
                _ligaSeleccionada.value = liga?.copy(id = snapshot.id)

                _cargando.value = false
            }
    }

    fun limpiarLigaSeleccionada() {
        _ligaSeleccionada.value = null
        _miembrosLiga.value = emptyList()
    }

    fun unirseALiga(ligaId: String, nombreEquipo: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val updates = mapOf(
            "idsMiembros" to com.google.firebase.firestore.FieldValue.arrayUnion(userId),
            "nombresEquipos.$userId" to nombreEquipo
        )

        FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
            .update(updates)
    }

    fun ficharAgenteLibre(ligaId: String, agenteId: String, nombreEquipo: String) {
        val db = FirebaseFirestore.getInstance()
        val ligaRef = db.collection("ligas").document(ligaId)

        val actualizaciones = mapOf(
            "idsAgentesLibres" to FieldValue.arrayRemove(agenteId),
            "idsMiembros" to FieldValue.arrayUnion(agenteId),
            "nombresEquipos.$agenteId" to nombreEquipo
        )

        ligaRef.update(actualizaciones).addOnSuccessListener {
            println("¡Jugador fichado con éxito por el equipo $nombreEquipo!")
        }
    }

    fun salirDeLiga(ligaId: String, onVolver: () -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ligaActual = _ligaSeleccionada.value ?: return

        val db = FirebaseFirestore.getInstance()
        val ligaRef = db.collection("ligas").document(ligaId)

        if (ligaActual.creadorId == userId) {
            val miembrosRestantes = ligaActual.idsMiembros.filter { it != userId }

            if (miembrosRestantes.isEmpty()) {
                ligaRef.delete().addOnSuccessListener {
                    onVolver()
                }
            } else {
                val nuevoAdminId = miembrosRestantes.first()

                val actualizaciones = mapOf(
                    "idsMiembros" to FieldValue.arrayRemove(userId),
                    "creadorId" to nuevoAdminId
                )

                ligaRef.update(actualizaciones).addOnSuccessListener {
                    onVolver()
                }
            }
        } else {
            ligaRef.update("idsMiembros", FieldValue.arrayRemove(userId)).addOnSuccessListener {
                onVolver()
            }
        }
    }

    private val _agentesLibres = MutableStateFlow<List<AgenteLibreUI>>(emptyList())
    val agentesLibres: StateFlow<List<AgenteLibreUI>> = _agentesLibres.asStateFlow()

    fun cargarAgentesLibres(ids: List<String>) {
        if (ids.isEmpty()) {
            _agentesLibres.value = emptyList()
            return
        }

        FirebaseFirestore.getInstance().collection("usuarios")
            .whereIn(com.google.firebase.firestore.FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val nombre = doc.getString("apodo") ?: doc.getString("nombre") ?: "Jugador"
                    val email = doc.getString("email") ?: doc.getString("correo") ?: "correo@ejemplo.com"
                    AgenteLibreUI(id, nombre, email)
                }
                _agentesLibres.value = lista
            }
    }

    fun apuntarseComoAgenteLibre(ligaId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
            .update("idsAgentesLibres", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
    }

    fun salirDeAgentesLibres(ligaId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
            .update("idsAgentesLibres", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
    }

    private val _partidosLiga = MutableStateFlow<List<Partido>>(emptyList())
    val partidosLiga: StateFlow<List<Partido>> = _partidosLiga.asStateFlow()

    fun crearPartido(partido: Partido) {
        val db = FirebaseFirestore.getInstance()
        val ref = db.collection("partidos").document()
        val partidoConId = partido.copy(id = ref.id)

        ref.set(partidoConId).addOnSuccessListener {
            cargarPartidosLiga(partido.idLiga)
        }
    }

    fun cargarPartidosLiga(ligaId: String) {
        FirebaseFirestore.getInstance().collection("partidos")
            .whereEqualTo("idLiga", ligaId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val lista = snapshot?.documents?.mapNotNull { it.toObject(Partido::class.java) } ?: emptyList()
                _partidosLiga.value = lista.sortedBy { it.jornada }
            }
    }

    fun finalizarPartido(partidoId: String, golesL: Int, golesV: Int, goleadoresMapa: Map<String, Int>) {
        val db = FirebaseFirestore.getInstance()
        val partidoRef = db.collection("partidos").document(partidoId)

        partidoRef.update(
            mapOf(
                "golesLocal" to golesL,
                "golesVisitante" to golesV,
                "estado" to "FINALIZADO",
                "goleadores" to goleadoresMapa
            )
        ).addOnSuccessListener {
            println("Resultado guardado")
        }
    }

    fun calcularPuntos(equipoId: String, partidos: List<Partido>): Int {
        var puntos = 0
        partidos.filter { it.estado == "FINALIZADO" }.forEach { p ->
            when {
                p.idLocal == equipoId && p.golesLocal > p.golesVisitante -> puntos += 3
                p.idVisitante == equipoId && p.golesVisitante > p.golesLocal -> puntos += 3
                (p.idLocal == equipoId || p.idVisitante == equipoId) && p.golesLocal == p.golesVisitante -> puntos += 1
            }
        }
        return puntos
    }

    fun generarCalendario(ligaId: String, miembros: List<MiembroUI>) {
        if (miembros.size < 2) return

        val equipos = miembros.toMutableList()
        if (equipos.size % 2 != 0) {
            equipos.add(MiembroUI("DESCANSA", "Descansa", false, "Descansa"))
        }

        val numRondas = equipos.size - 1
        val mitad = equipos.size / 2
        val partidosNuevos = mutableListOf<Partido>()

        for (ronda in 0 until numRondas) {
            for (i in 0 until mitad) {
                val local = equipos[i]
                val visitante = equipos[equipos.size - 1 - i]

                if (local.id != "DESCANSA" && visitante.id != "DESCANSA") {
                    partidosNuevos.add(
                        Partido(
                            idLiga = ligaId,
                            jornada = ronda + 1,
                            idLocal = local.id,
                            idVisitante = visitante.id,
                            nombreLocal = local.nombreEquipo,
                            nombreVisitante = visitante.nombreEquipo,
                            estado = "PENDIENTE"
                        )
                    )
                }
            }
            val ultimo = equipos.removeAt(equipos.size - 1)
            equipos.add(1, ultimo)
        }

        val db = FirebaseFirestore.getInstance()
        val batch = db.batch()

        partidosNuevos.forEach { partido ->
            val ref = db.collection("partidos").document()
            batch.set(ref, partido.copy(id = ref.id))
        }

        val ligaRef = db.collection("ligas").document(ligaId)
        batch.update(ligaRef, "estado", "EN_JUEGO")

        batch.commit().addOnSuccessListener {
            println("¡Calendario generado con éxito!")
            cargarPartidosLiga(ligaId)
        }
    }

    fun cambiarNombreEquipo(ligaId: String, equipoId: String, nuevoNombre: String) {
        val db = FirebaseFirestore.getInstance()
        val ligaRef = db.collection("ligas").document(ligaId)

        // 1. Cambiamos el nombre en la liga
        ligaRef.update("nombresEquipos.$equipoId", nuevoNombre).addOnSuccessListener {

            // 2. Buscamos TODOS los partidos de esa liga
            db.collection("partidos")
                .whereEqualTo("idLiga", ligaId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val batch = db.batch()
                    var partidosModificados = 0

                    for (doc in snapshot.documents) {
                        val idLocal = doc.getString("idLocal")
                        val idVisitante = doc.getString("idVisitante")

                        // Si encuentra tu equipo, lo actualiza a la fuerza
                        if (idLocal == equipoId) {
                            batch.update(doc.reference, "nombreLocal", nuevoNombre)
                            partidosModificados++
                        }
                        if (idVisitante == equipoId) {
                            batch.update(doc.reference, "nombreVisitante", nuevoNombre)
                            partidosModificados++
                        }
                    }

                    // 3. Ejecutamos los cambios
                    if (partidosModificados > 0) {
                        batch.commit().addOnSuccessListener {
                            println("¡Forzado! $partidosModificados partidos actualizados.")
                            cargarPartidosLiga(ligaId)
                        }
                    }
                }
        }
    }

}