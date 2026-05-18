package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SocialViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _mensajeUI = MutableStateFlow<String?>(null)
    val mensajeUI: StateFlow<String?> = _mensajeUI

    fun buscarYEnviarSolicitud(codigoBusqueda: String) {
        val miId = auth.currentUser?.uid ?: return

        if (codigoBusqueda.isBlank()) {
            _mensajeUI.value = "Por favor, introduce un código."
            return
        }

        db.collection("usuarios")
            .whereEqualTo("codigoAmigo", codigoBusqueda)
            .get()
            .addOnSuccessListener { documentos ->
                if (documentos.isEmpty) {
                    _mensajeUI.value = "No se ha encontrado ningún usuario con ese código."
                    return@addOnSuccessListener
                }

                val usuarioEncontrado = documentos.documents.first()
                val idReceptor = usuarioEncontrado.id
                val apodoReceptor = usuarioEncontrado.getString("apodo") ?: "Usuario"

                if (idReceptor == miId) {
                    _mensajeUI.value = "No puedes enviarte una solicitud a ti mismo."
                    return@addOnSuccessListener
                }

                val nuevaSolicitud = mapOf(
                    "idEmisor" to miId,
                    "idReceptor" to idReceptor,
                    "estado" to "PENDIENTE",
                    "fecha" to System.currentTimeMillis()
                )

                val idSolicitud = "${miId}_${idReceptor}"

                db.collection("solicitudes").document(idSolicitud)
                    .set(nuevaSolicitud)
                    .addOnSuccessListener {
                        _mensajeUI.value = "¡Solicitud enviada a $apodoReceptor!"
                    }
                    .addOnFailureListener {
                        _mensajeUI.value = "Error al enviar la solicitud."
                    }
            }
            .addOnFailureListener {
                _mensajeUI.value = "Error de conexión al buscar."
            }
    }

    fun limpiarMensaje() {
        _mensajeUI.value = null
    }
}