package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import com.example.rivalry.domain.model.PartidoSuelto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class PartidoSueltoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _misPartidos = MutableStateFlow<List<PartidoSuelto>>(emptyList())
    val misPartidos: StateFlow<List<PartidoSuelto>> = _misPartidos

    private val _partidosExplorar = MutableStateFlow<List<PartidoSuelto>>(emptyList())
    val partidosExplorar: StateFlow<List<PartidoSuelto>> = _partidosExplorar

    init {
        obtenerPartidos()
    }

    private fun obtenerPartidos() {
        db.collection("partidosSueltos")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val userId = auth.currentUser?.uid ?: return@addSnapshotListener
                val listaMisPartidos = mutableListOf<PartidoSuelto>()
                val listaExplorar = mutableListOf<PartidoSuelto>()

                for (document in snapshot.documents) {
                    val partido = document.toObject(PartidoSuelto::class.java)?.copy(id = document.id)
                    if (partido != null) {
                        if (partido.idsJugadores.contains(userId)) {
                            listaMisPartidos.add(partido)
                        } else {
                            listaExplorar.add(partido)
                        }
                    }
                }

                _misPartidos.value = listaMisPartidos
                _partidosExplorar.value = listaExplorar
            }
    }

    fun crearPartido(deporte: String,
                     maxJugadores: Int,
                     fecha: String,
                     hora: String,
                     ubicacion: String,
                     nivel: String,
                     provincia: String,
                     ciudad: String,
                     onExito: () -> Unit
    ) {
        val userId = auth.currentUser?.uid ?: return

        val nuevoPartido = PartidoSuelto(
            deporte = deporte,
            creadorId = userId,
            idsJugadores = listOf(userId),
            maxJugadores = maxJugadores,
            fecha = fecha,
            hora = hora,
            ubicacion = ubicacion,
            nivel = nivel,
            provincia = provincia,
            ciudad = ciudad
        )

        db.collection("partidosSueltos").add(nuevoPartido).addOnSuccessListener {
            onExito()
        }
    }

    fun unirseAPartido(partidoId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("partidosSueltos").document(partidoId)
            .update("idsJugadores", FieldValue.arrayUnion(userId))
    }

    fun salirDePartido(partidoId: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("partidosSueltos").document(partidoId)
            .update("idsJugadores", FieldValue.arrayRemove(userId))
    }
}