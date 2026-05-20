package com.example.rivalry.presentation.auth.home

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileOutputStream

class PerfilViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _perfil = MutableStateFlow(UsuarioPerfil())
    val perfil: StateFlow<UsuarioPerfil> = _perfil.asStateFlow()

    fun cargarPerfil() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("usuarios").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    val perfilDescargado = snapshot.toObject(UsuarioPerfil::class.java)
                    if (perfilDescargado != null) {
                        _perfil.value = perfilDescargado
                    }
                }
            }
    }

    fun guardarPerfil(apodo: String, posicion: String, dorsal: String, bio: String) {
        val userId = auth.currentUser?.uid ?: return

        val updates = mapOf(
            "apodo" to apodo,
            "posicion" to posicion,
            "dorsal" to dorsal,
            "bio" to bio
        )

        db.collection("usuarios").document(userId)
            .set(updates, SetOptions.merge())
    }

    fun subirFotoPerfil(context: android.content.Context, uri: Uri) {
        val userId = auth.currentUser?.uid ?: return

        try {
            val tiempo = System.currentTimeMillis()
            val nombreArchivo = "perfil_${userId}_${tiempo}.jpg"
            val archivoLocal = File(context.filesDir, nombreArchivo)

            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(archivoLocal)
            inputStream?.copyTo(outputStream)

            inputStream?.close()
            outputStream.close()

            val rutaPura = archivoLocal.absolutePath
            val datosFoto = mapOf("avatarUrl" to rutaPura)

            db.collection("usuarios").document(userId)
                .set(datosFoto, SetOptions.merge())

        } catch (e: Exception) {
            println("Error en fichero local de perfil: ${e.message}")
        }
    }
}