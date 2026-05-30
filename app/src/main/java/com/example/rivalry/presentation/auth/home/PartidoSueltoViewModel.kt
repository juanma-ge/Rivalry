package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import com.example.rivalry.domain.model.PartidoSuelto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.rivalry.domain.model.Partido

class PartidoSueltoViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _misPartidos = MutableStateFlow<List<PartidoSuelto>>(emptyList())
    val misPartidos: StateFlow<List<PartidoSuelto>> = _misPartidos

    private val _partidosExplorar = MutableStateFlow<List<PartidoSuelto>>(emptyList())
    val partidosExplorar: StateFlow<List<PartidoSuelto>> = _partidosExplorar

    private val _misPartidosSueltos = MutableStateFlow<List<PartidoSuelto>>(emptyList())
    val misPartidosSueltos: StateFlow<List<PartidoSuelto>> = _misPartidosSueltos

    private val _partidosLiga = MutableStateFlow<List<Partido>>(emptyList())
    val partidosLiga: StateFlow<List<Partido>> = _partidosLiga

    init {
        escucharPartidosSueltos()
        escucharPartidosDeLiga()
    }

    private fun escucharPartidosSueltos() {
        val miId = auth.currentUser?.uid ?: return

        db.collection("partidosSueltos")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val todosLosPartidos = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PartidoSuelto::class.java)?.copy(id = doc.id)
                }

                _misPartidosSueltos.value = todosLosPartidos.filter { it.idsJugadores.contains(miId) }

                _partidosExplorar.value = todosLosPartidos.filter { !it.idsJugadores.contains(miId) }
            }
    }

    private fun escucharPartidosDeLiga() {
        val miId = auth.currentUser?.uid ?: return

        db.collection("ligas")
            .whereArrayContains("idsMiembros", miId)
            .addSnapshotListener { snapshotLigas, errorLigas ->
                if (errorLigas != null || snapshotLigas == null) return@addSnapshotListener

                val idsMisLigas = snapshotLigas.documents.map { it.id }

                if (idsMisLigas.isEmpty()) {
                    _partidosLiga.value = emptyList()
                    return@addSnapshotListener
                }

                db.collection("partidos")
                    .whereIn("idLiga", idsMisLigas)
                    .addSnapshotListener { snapshotPartidos, errorPartidos ->
                        if (errorPartidos != null || snapshotPartidos == null) return@addSnapshotListener

                        val todosLosPartidosLiga = snapshotPartidos.documents.mapNotNull { doc ->
                            doc.toObject(Partido::class.java)?.copy(id = doc.id)
                        }

                        _partidosLiga.value = todosLosPartidosLiga
                    }
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