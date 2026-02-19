package com.example.rivalry.data.repository

import android.util.Log
import com.example.rivalry.domain.model.Partido
import com.example.rivalry.domain.repository.PartidoRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PartidorepositoryImpl(private val db: FirebaseFirestore): PartidoRepository {

    override suspend fun crearPartido(partido: Partido): Boolean {
        return try{
            val docRef = db.collection("partidos").document()
            val partidoConId = partido.copy(id = docRef.id)
            docRef.set(partidoConId).await()
            true
        } catch (e: Exception){
            Log.e("PartidoRepo", "Error creando partido", e)
            false
        }
    }

    override suspend fun obtenerPartidoLiga(idLiga: String): List<Partido> {
        return try{
           val snapshot = db.collection("partidos")
               .whereEqualTo("idLiga", idLiga)
               .get().await()
            snapshot.toObjects(Partido::class.java)
        } catch(e: Exception){
            Log.e("PartidoRepo", "Error obteniendo partidos", e)
            emptyList()
        }
    }

}