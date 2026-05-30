package com.example.rivalry.presentation.auth.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AgenteLibreUI(val id: String, val nombre: String, val email: String)

private val _agentesLibres = MutableStateFlow<List<AgenteLibreUI>>(emptyList())
val agentesLibres: StateFlow<List<AgenteLibreUI>> = _agentesLibres.asStateFlow()

fun cargarAgentesLibres(ids: List<String>) {
    if (ids.isEmpty()) {
        _agentesLibres.value = emptyList()
        return
    }

    FirebaseFirestore.getInstance().collection("usuarios")
        .whereIn(FieldPath.documentId(), ids)
        .get()
        .addOnSuccessListener { snapshot ->
            val lista = snapshot.documents.mapNotNull { doc ->
                val id = doc.id
                val nombre = doc.getString("nombre") ?: doc.getString("nombreUsuario") ?: "Jugador"
                val email = doc.getString("email") ?: doc.getString("correo") ?: "correo@ejemplo.com"
                AgenteLibreUI(id, nombre, email)
            }
            _agentesLibres.value = lista
        }
}

fun apuntarseComoAgenteLibre(ligaId: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
        .update("idsAgentesLibres", FieldValue.arrayUnion(userId))
}

fun salirDeAgentesLibres(ligaId: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    FirebaseFirestore.getInstance().collection("ligas").document(ligaId)
        .update("idsAgentesLibres", FieldValue.arrayRemove(userId))
}