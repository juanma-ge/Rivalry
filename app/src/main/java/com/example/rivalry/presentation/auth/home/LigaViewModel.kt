package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rivalry.domain.model.Deporte
import com.example.rivalry.domain.model.Liga
import com.example.rivalry.domain.model.MiembroUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FieldValue

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

    fun cargarMiembros(ids: List<String>, creadorId: String?) {
        if (ids.isEmpty()) {
            _miembrosLiga.value = emptyList()
            return
        }

        FirebaseFirestore.getInstance().collection("usuarios")
            .whereIn(com.google.firebase.firestore.FieldPath.documentId(), ids)
            .get()
            .addOnSuccessListener { snapshot ->
                val listaConRoles = snapshot.documents.mapNotNull { doc ->
                    val id = doc.id
                    val nombre = doc.getString("nombre") ?: doc.getString("nombreUsuario") ?: "Usuario"
                    MiembroUI(id = id, nombre = nombre, esAdmin = id == creadorId)
                }
                _miembrosLiga.value = listaConRoles
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

    fun unirseALiga(ligaId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
            .update("idsMiembros", FieldValue.arrayUnion(userId))
            .addOnSuccessListener {
            }
            .addOnFailureListener {
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

}