package com.example.rivalry.data.repository

import android.util.Log
import com.example.rivalry.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(private val auth: FirebaseAuth): AuthRepository {

    override suspend fun login(email: String, contrasenia: String): String? {
        return try{
            val result = auth.signInWithEmailAndPassword(email, contrasenia).await()
            result.user?.uid
        } catch(e: Exception){
            Log.e("AuthRepo", "Error en el login", e)
            null
        }
    }

    override suspend fun registro(email: String, contrasenia: String): String? {
        return try{
            val result = auth.createUserWithEmailAndPassword(email, contrasenia).await()
            result.user?.uid
        }catch(e: Exception){
            Log.e("AuthRepo", "Error en el registro", e)
            null
        }
    }

    override fun obtenerIdUsuarioActual(): String? {
        return auth.currentUser?.uid
    }

    override fun cerrarSesion() {
        auth.signOut()
    }

}