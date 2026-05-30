package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import com.example.rivalry.presentation.auth.model.AmigoUI
import com.example.rivalry.presentation.auth.model.SolicitudUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SocialViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _mensajeUI = MutableStateFlow<String?>(null)
    val mensajeUI: StateFlow<String?> = _mensajeUI

    private val _solicitudes = MutableStateFlow<List<SolicitudUI>>(emptyList())
    val solicitudes: StateFlow<List<SolicitudUI>> = _solicitudes

    private val _amigosLista = MutableStateFlow<List<AmigoUI>>(emptyList())
    val amigosLista: StateFlow<List<AmigoUI>> = _amigosLista

    init {
        cargarSolicitudesPendientes()
        escucharAmigosEnTiempoReal()
    }

    private fun escucharAmigosEnTiempoReal() {
        val miId = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(miId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener

                val idsAmigos = snapshot.get("amigos") as? List<String> ?: emptyList()

                if (idsAmigos.isEmpty()) {
                    _amigosLista.value = emptyList()
                    return@addSnapshotListener
                }

                val listaTemporal = mutableListOf<AmigoUI>()
                var procesados = 0

                idsAmigos.forEach { idAmigo ->
                    db.collection("usuarios").document(idAmigo).get()
                        .addOnSuccessListener { userDoc ->
                            if (userDoc.exists()) {
                                val nombre = userDoc.getString("apodo") ?: userDoc.getString("nombre") ?: "Usuario"
                                val avatarUrl = userDoc.getString("avatarUrl") ?: ""
                                listaTemporal.add(AmigoUI(idAmigo, nombre, avatarUrl))
                            }
                            procesados++
                            if (procesados == idsAmigos.size) {
                                _amigosLista.value = listaTemporal.toList()
                            }
                        }
                }
            }
    }

    private fun cargarSolicitudesPendientes() {
        val miId = auth.currentUser?.uid ?: return

        db.collection("solicitudes")
            .whereEqualTo("idReceptor", miId)
            .whereEqualTo("estado", "PENDIENTE")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                val documentos = snapshot.documents
                if (documentos.isEmpty()) {
                    _solicitudes.value = emptyList()
                    return@addSnapshotListener
                }

                val solicitudesTemporales = mutableListOf<SolicitudUI>()

                documentos.forEach { doc ->
                    val idEmisor = doc.getString("idEmisor") ?: return@forEach
                    val idSolicitud = doc.id

                    db.collection("usuarios").document(idEmisor).get()
                        .addOnSuccessListener { userDoc ->
                            val nombre = userDoc.getString("apodo") ?: userDoc.getString("nombre") ?: "Usuario"
                            solicitudesTemporales.add(SolicitudUI(idSolicitud, idEmisor, nombre))
                            _solicitudes.value = solicitudesTemporales.toList()
                        }
                }
            }
    }

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
                    .addOnFailureListener { _mensajeUI.value = "Error al enviar la solicitud." }
            }
            .addOnFailureListener { _mensajeUI.value = "Error de conexión al buscar." }
    }

    fun responderSolicitud(idSolicitud: String, aceptar: Boolean) {
        val miId = auth.currentUser?.uid ?: return

        if (aceptar) {
            db.collection("solicitudes").document(idSolicitud).get()
                .addOnSuccessListener { doc ->
                    val idEmisor = doc.getString("idEmisor") ?: return@addOnSuccessListener

                    // Añadimos a las dos listas cruzadas
                    db.collection("usuarios").document(idEmisor)
                        .update("amigos", FieldValue.arrayUnion(miId))

                    db.collection("usuarios").document(miId)
                        .update("amigos", FieldValue.arrayUnion(idEmisor))
                        .addOnSuccessListener {
                            db.collection("solicitudes").document(idSolicitud)
                                .update("estado", "ACEPTADA")
                        }
                }
        } else {
            db.collection("solicitudes").document(idSolicitud).update("estado", "RECHAZADA")
        }
    }

    fun limpiarMensaje() {
        _mensajeUI.value = null
    }

    fun obtenerOCrearChatPrivado(idOtroUsuario: String, onExito: (String) -> Unit) {
        val miId = auth.currentUser?.uid ?: return
        if (miId == idOtroUsuario) return

        val idChatPrivado = if (miId < idOtroUsuario) "${miId}_${idOtroUsuario}" else "${idOtroUsuario}_${miId}"
        val chatRef = db.collection("chatsPrivados").document(idChatPrivado)

        chatRef.get().addOnSuccessListener { documento ->
            if (documento.exists()) {
                onExito(idChatPrivado)
            } else {
                val nuevoChat = mapOf(
                    "idChat" to idChatPrivado,
                    "participantes" to listOf(miId, idOtroUsuario),
                    "ultimoMensaje" to "",
                    "fechaUltimoMensaje" to System.currentTimeMillis()
                )
                chatRef.set(nuevoChat).addOnSuccessListener {
                    onExito(idChatPrivado)
                }
            }
        }
    }
}