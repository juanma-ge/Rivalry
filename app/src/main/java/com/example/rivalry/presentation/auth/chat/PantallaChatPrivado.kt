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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rivalry.domain.model.MensajePrivado
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaChatPrivado(
    idChat: String,
    nombreAmigo: String,
    avatarUrl: String,
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
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    listaMensajes = snapshot.documents.mapNotNull { doc ->
                        try { doc.toObject(MensajePrivado::class.java)?.copy(id = doc.id) }
                        catch (e: Exception) { null }
                    }
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }

            // Foto de perfil del amigo en el chat
            Box(
                modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val rutaLimpia = avatarUrl.replace("file://", "")
                val archivoFoto = java.io.File(rutaLimpia)
                if (rutaLimpia.isNotEmpty() && archivoFoto.exists()) {
                    AsyncImage(
                        model = archivoFoto,
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize().background(Color.Transparent, CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Sin foto", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))
            Text(nombreAmigo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(listaMensajes) { msg ->
                val esMio = msg.idEmisor == miId

                // Formateamos la hora del mensaje
                val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
                val horaString = formatoHora.format(Date(msg.fecha))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (esMio) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 12.dp, topEnd = 12.dp,
                            bottomStart = if (esMio) 12.dp else 0.dp,
                            bottomEnd = if (esMio) 0.dp else 12.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (esMio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                            Text(
                                text = msg.texto,
                                color = if (esMio) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                            Text(
                                text = horaString,
                                color = if (esMio) Color.White.copy(alpha = 0.7f) else Color.Gray,
                                fontSize = 10.sp,
                                modifier = Modifier.align(Alignment.End).padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- BARRA DE ESCRIBIR ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textoMensaje,
                onValueChange = { textoMensaje = it },
                placeholder = { Text("Escribe un mensaje...") },
                modifier = Modifier.weight(1f).height(50.dp),
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (textoMensaje.isNotBlank()) {
                        val nuevoMsg = mapOf("idEmisor" to miId, "texto" to textoMensaje.trim(), "fecha" to System.currentTimeMillis())
                        val db = FirebaseFirestore.getInstance()
                        db.collection("chatsPrivados").document(idChat).collection("mensajes").add(nuevoMsg)
                        db.collection("chatsPrivados").document(idChat).update(
                            mapOf("ultimoMensaje" to textoMensaje.trim(), "fechaUltimoMensaje" to System.currentTimeMillis())
                        )
                        textoMensaje = ""
                    }
                },
                modifier = Modifier.size(44.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}