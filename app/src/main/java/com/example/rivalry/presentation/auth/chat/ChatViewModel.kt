package com.example.rivalry.presentation.auth.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rivalry.domain.model.Mensaje
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _mensajes = MutableStateFlow<List<Mensaje>>(emptyList())
    val mensajes: StateFlow<List<Mensaje>> = _mensajes

    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    fun cargarMensajes(salaId: String) {
        listenerRegistration?.remove()

        listenerRegistration = db.collection("mensajes")
            .whereEqualTo("salaId", salaId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error al cargar mensajes: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val lista = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Mensaje::class.java)?.copy(id = doc.id)
                    }
                    _mensajes.value = lista.sortedBy { it.timestamp }
                }
            }
    }

    fun enviarMensaje(salaId: String, texto: String) {
        if (texto.isBlank()) return

        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val emailUsuario = auth.currentUser?.email?.substringBefore("@") ?: "Jugador"

            val nuevoMensaje = Mensaje(
                salaId = salaId,
                remitenteId = userId,
                remitenteNombre = emailUsuario,
                texto = texto,
                timestamp = System.currentTimeMillis()
            )

            db.collection("mensajes").add(nuevoMensaje)
                .addOnSuccessListener {
                    println("Mensaje enviado con éxito")
                }
                .addOnFailureListener { e ->
                    println("Error al enviar: ${e.message}")
                }
        }
    }

    fun limpiarChat() {
        listenerRegistration?.remove()
        _mensajes.value = emptyList()
    }
}