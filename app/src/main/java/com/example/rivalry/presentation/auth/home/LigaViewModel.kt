package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rivalry.domain.model.Deporte
import com.example.rivalry.domain.model.Liga
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
                    idsMiembros = listOf(creadorId)
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

    fun salirDeLiga(ligaId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
            .update("idsMiembros", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
            }
    }

}