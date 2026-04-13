package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rivalry.domain.model.Deporte
import com.example.rivalry.domain.model.Liga
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

        _misLigas.value = listOf(
            Liga(nombre = "Liga de Barrio 1", deporte = Deporte.FUTBOL_7.name, maxParticipantes = 12, esPublica = false),
            Liga(nombre = "Torneo Oficina", deporte = Deporte.PADEL.name, maxParticipantes = 8, esPublica = false)
        )

        _ligasExplorar.value = listOf(
            Liga(nombre = "Torneo Verano Ciudad", deporte = Deporte.FUTBOL_SALA.name, maxParticipantes = 10, esPublica = true),
            Liga(nombre = "Liga Universitaria", deporte = Deporte.BALONCESTO.name, maxParticipantes = 16, esPublica = true),
            Liga(nombre = "Copa Nocturna", deporte = Deporte.FUTBOL_7.name, maxParticipantes = 8, esPublica = true)
        )

        _cargando.value = false
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

}