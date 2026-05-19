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

@OptIn(ExperimentalMaterial3Api::class)
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
            .orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    listaMensajes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MensajePrivado::class.java)?.copy(id = doc.id)
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mensaje Directo", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
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
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textoMensaje,
                    onValueChange = { textoMensaje = it },
                    placeholder = { Text("Escribe un mensaje privado...") },
                    modifier = Modifier.weight(1f),
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
                            val db = FirebaseFirestore.getInstance()

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
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White)
                }
            }
        }
    }
}