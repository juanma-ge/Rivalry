package com.example.rivalry.presentation.auth.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.MensajePrivado
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@Composable
fun PantallaChatPrivado(
    idChat: String,
    onVolver: () -> Unit
) {
    val miId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var textoMensaje by remember { mutableStateOf("") }
    var listaMensajes by remember { mutableStateOf<List<MensajePrivado>>(emptyList()) }

    LaunchedEffect(idChat) {
        FirebaseFirestore.getInstance()
            .collection("chatsPrivados")
            .document(idChat)
            .collection("mensajes")
            .orderBy("fecha", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    listaMensajes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MensajePrivado::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 4.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Text("Mensaje Directo", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(listaMensajes) { msg ->
                val esMio = msg.idEmisor == miId
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (esMio) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (esMio) 12.dp else 0.dp,
                            bottomEnd = if (esMio) 0.dp else 12.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esMio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.texto,
                            color = if (esMio) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), // Textos más compactos
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textoMensaje,
                onValueChange = { textoMensaje = it },
                placeholder = { Text("Escribe un mensaje...") },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (textoMensaje.isNotBlank()) {
                        val nuevoMsg = mapOf(
                            "idEmisor" to miId,
                            "texto" to textoMensaje.trim(),
                            "fecha" to System.currentTimeMillis()
                        )
                        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()

                        db.collection("chatsPrivados").document(idChat)
                            .collection("mensajes").add(nuevoMsg)

                        db.collection("chatsPrivados").document(idChat).update(
                            mapOf(
                                "ultimoMensaje" to textoMensaje.trim(),
                                "fechaUltimoMensaje" to System.currentTimeMillis()
                            )
                        )
                        textoMensaje = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}