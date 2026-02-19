package com.example.rivalry.data.repository

import android.util.Log
import com.example.rivalry.domain.model.Liga
import com.example.rivalry.domain.repository.LigaRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LigaRepositoryImpl(private val db: FirebaseFirestore): LigaRepository {

    override suspend fun crearLiga(liga: Liga): Boolean {
        return try {
            val docRef = db.collection("ligas").document()
            val ligaConId = liga.copy(id = docRef.id)
            docRef.set(ligaConId).await()
            true
        } catch(e: Exception){
            Log.e("LigaRepo", "Error creando liga", e)
            false
        }
    }

    override suspend fun obtenerLigasDelUsuario(idUsuario: String): List<Liga> {
        return try {
            val snapshot = db.collection("ligas")
                .whereArrayContains("idsMiembros", idUsuario)
                .get().await()
            snapshot.toObjects(Liga::class.java)
        } catch(e: Exception){
            Log.e("LigaRepo", "Error obteniendo ligas", e)
            emptyList()
        }
    }

}