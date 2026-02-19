package com.example.rivalry.data.repository

import com.example.rivalry.domain.model.Liga
import com.example.rivalry.domain.repository.LigaRepository
import com.google.firebase.firestore.FirebaseFirestore

class LigaRepositoryImpl(private val db: FirebaseFirestore): LigaRepository {

    override suspend fun crearLiga(liga: Liga): Boolean {
        return try {

        }
    }

}