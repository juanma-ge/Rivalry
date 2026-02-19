package com.example.rivalry.data.repository

import android.util.Log
import com.example.rivalry.domain.model.Equipo
import com.example.rivalry.domain.model.Liga
import com.example.rivalry.domain.repository.EquipoRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EquipoRepositoryImpl(private val db: FirebaseFirestore): EquipoRepository {

    override suspend fun crearEquipo(equipo: Equipo): Boolean {
        return try {
            val docRef = db.collection("equipos").document()
            val equipoConid = equipo.copy(id = docRef.id)
            docRef.set(equipoConid).await()
            true
        } catch(e: Exception){
            Log.e("EquipoRepo", "Error creando equipo", e)
            false
        }
    }

    override suspend fun obtenerEquiposLiga(idLiga: Liga): List<Equipo> {
        return try {
            val snapshot = db.collection("equipos")
                .whereEqualTo("idLiga", idLiga)
                .get().await()
            snapshot.toObjects(Equipo::class.java)
        } catch (e: Exception){
            Log.e("EquipoRepo", "Error obteniendo equipos", e)
            emptyList()
        }
    }

    override suspend fun actualizarEquipo(equipo: Equipo): Boolean {
        return try{
            db.collection("equipos").document(equipo.id).set(equipo).await()
            true
        } catch(e: Exception){
            Log.e("EquipoRepo", "Error actualizando equipo", e)
            false
        }
    }

}