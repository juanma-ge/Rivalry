package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rivalry.domain.model.Deporte
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
                    val nombre = doc.getString("nombre") ?: doc.getString("nombreUsuario") ?: "Jugador"
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
                    val nombre = doc.getString("nombre") ?: doc.getString("nombreUsuario") ?: "Jugador"
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

    fun finalizarPartido(partidoId: String, gLocal: Int, gVisitante: Int, goleadores: Map<String, Int>) {
        val updates = mapOf(
            "golesLocal" to gLocal,
            "golesVisitante" to gVisitante,
            "goleadores" to goleadores,
            "estado" to "FINALIZADO"
        )
        FirebaseFirestore.getInstance().collection("partidos").document(partidoId).update(updates)
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

}