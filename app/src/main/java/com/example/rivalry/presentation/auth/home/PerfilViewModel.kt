package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PerfilViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _perfil = MutableStateFlow(UsuarioPerfil())
    val perfil: StateFlow<UsuarioPerfil> = _perfil

    fun cargarPerfil() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("usuarios").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val apodo = document.getString("apodo") ?: ""
                    val posicion = document.getString("posicion") ?: "MED"
                    val dorsal = document.getString("dorsal") ?: "8"
                    val bio = document.getString("bio") ?: "Buscando equipo para los fines de semana."
                    _perfil.value = UsuarioPerfil(apodo, posicion, dorsal, bio)
                }
            }
    }

    fun guardarPerfil(apodo: String, posicion: String, dorsal: String, bio: String) {
        val userId = auth.currentUser?.uid ?: return
        val codigoAmigo = "RIV-${userId.take(5).uppercase()}"

        val datosUsuario = mapOf(
            "apodo" to apodo,
            "posicion" to posicion,
            "dorsal" to dorsal,
            "bio" to bio,
            "codigoAmigo" to codigoAmigo
        )

        db.collection("usuarios").document(userId)
            .set(datosUsuario, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                _perfil.value = UsuarioPerfil(apodo, posicion, dorsal, bio)
            }
    }

    fun subirFotoPerfil(uri: android.net.Uri) {
        val userId = auth.currentUser?.uid ?: return
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
        val fotoRef = storageRef.child("fotos_perfil/$userId.jpg")

        fotoRef.putFile(uri).addOnSuccessListener {
            fotoRef.downloadUrl.addOnSuccessListener { url ->
                db.collection("usuarios").document(userId)
                    .update("avatarUrl", url.toString())
                    .addOnSuccessListener {
                        cargarPerfil()
                    }
            }
        }.addOnFailureListener {
            println("Error al subir la foto de perfil: ${it.message}")
        }
    }

}