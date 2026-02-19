package com.example.rivalry.data.repository

import android.util.Log
import com.example.rivalry.domain.model.Usuario
import com.example.rivalry.domain.repository.UsuarioRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UsuarioRepositoryImpl(private val db: FirebaseFirestore): UsuarioRepository {

    override suspend fun guardarUsuario(usuario: Usuario): Boolean {
        return try{
            db.collection("usuarios").document(usuario.id).set(usuario).await()
            true
        } catch (e: Exception){
            Log.e("UsuarioRepo", "Error guardando usuario", e)
            false
        }
    }

    override suspend fun obtenerUsuario(id: String): Usuario? {
        return try{
            val documento = db.collection("usuarios").document(id).get().await()
            documento.toObject(Usuario::class.java)
        } catch (e: Exception){
            Log.e("UsuarioRepo", "Error obteniendo usuario", e)
            null
        }
    }

}