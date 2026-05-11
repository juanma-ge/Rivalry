package com.example.rivalry.presentation.auth.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Mensaje
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatIntegrado(salaId: String, viewModel: ChatViewModel, miId: String?) {
    val mensajes by viewModel.mensajes.collectAsState()
    var textoMensaje by remember { mutableStateOf("") }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    LaunchedEffect(salaId) {
        viewModel.cargarMensajes(salaId)
    }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listState.animateScrollToItem(mensajes.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(mensajes) { msg ->
                BurbujaMensaje(mensaje = msg, esMio = msg.remitenteId == miId)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textoMensaje,
                onValueChange = { textoMensaje = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Escribe un mensaje...") },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    viewModel.enviarMensaje(salaId, textoMensaje)
                    textoMensaje = ""
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar", tint = Color.White)
            }
        }
    }
}

@Composable
fun BurbujaMensaje(mensaje: Mensaje, esMio: Boolean) {
    val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())
    val horaString = formatoHora.format(Date(mensaje.timestamp))

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (esMio) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (esMio) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(
                        topStart = 16.dp, topEnd = 16.dp,
                        bottomStart = if (esMio) 16.dp else 0.dp,
                        bottomEnd = if (esMio) 0.dp else 16.dp
                    )
                )
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            if (!esMio) {
                Text(text = mensaje.remitenteNombre, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(text = mensaje.texto, fontSize = 16.sp)
            Text(text = horaString, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.align(Alignment.End).padding(top = 4.dp))
        }
    }
}