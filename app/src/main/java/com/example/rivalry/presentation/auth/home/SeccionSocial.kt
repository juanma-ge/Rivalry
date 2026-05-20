package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import coil.compose.AsyncImage
import com.example.rivalry.presentation.auth.chat.PantallaChatPrivado

@Composable
fun SeccionSocial(viewModel: SocialViewModel = viewModel()) {
    var codigoBusqueda by remember { mutableStateOf("") }
    var textoFiltroAmigos by remember { mutableStateOf("") }

    val mensajeUI by viewModel.mensajeUI.collectAsState()
    val solicitudes by viewModel.solicitudes.collectAsState()
    val amigosLista by viewModel.amigosLista.collectAsState()

    var idChatActivoId by remember { mutableStateOf<String?>(null) }

    val amigosFiltrados = remember(amigosLista, textoFiltroAmigos) {
        if (textoFiltroAmigos.isBlank()) {
            amigosLista
        } else {
            amigosLista.filter { amigo ->
                amigo.nombre.contains(textoFiltroAmigos, ignoreCase = true)
            }
        }
    }

    if (idChatActivoId != null) {
        PantallaChatPrivado(
            idChat = idChatActivoId!!,
            onVolver = { idChatActivoId = null } // Al volver, el IF devuelve el valor a null y reaparece la lista
        )
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Añadir Amigo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = codigoBusqueda,
                    onValueChange = { codigoBusqueda = it.uppercase() },
                    label = { Text("Ej: RIV-A1B2C") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.buscarYEnviarSolicitud(codigoBusqueda)
                        codigoBusqueda = ""
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }

            mensajeUI?.let { mensaje ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = mensaje,
                    color = if (mensaje.contains("Error") || mensaje.contains("No")) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                LaunchedEffect(mensaje) {
                    kotlinx.coroutines.delay(3000)
                    viewModel.limpiarMensaje()
                }
            }

            if (solicitudes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                Text("Solicitudes Pendientes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))

                solicitudes.forEach { solicitud ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("De: ${solicitud.nombreEmisor}", fontWeight = FontWeight.Bold)
                            Row {
                                IconButton(onClick = { viewModel.responderSolicitud(solicitud.idSolicitud, true) }) {
                                    Icon(Icons.Default.Check, contentDescription = "Aceptar", tint = Color(0xFF4CAF50))
                                }
                                IconButton(onClick = { viewModel.responderSolicitud(solicitud.idSolicitud, false) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Rechazar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Text("Mensajes Directos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = textoFiltroAmigos,
                onValueChange = { textoFiltroAmigos = it },
                placeholder = { Text("Buscar entre tus amigos...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Lupa amigos", tint = Color.Gray) },
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (amigosLista.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aún no tienes amigos.\n¡Busca a un amigo por su código para empezar a chatear!",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            } else if (amigosFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No se ha encontrado ningún amigo que coincida con '$textoFiltroAmigos'.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(amigosFiltrados) { amigo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.obtenerOCrearChatPrivado(amigo.id) { idChatGenerado ->
                                        idChatActivoId = idChatGenerado
                                    }
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val rutaFotoLimpia = amigo.avatarUrl.replace("file://", "")
                                    val archivoFoto = java.io.File(rutaFotoLimpia)

                                    if (rutaFotoLimpia.isNotEmpty() && archivoFoto.exists()) {
                                        AsyncImage(
                                            model = archivoFoto,
                                            contentDescription = "Avatar amigo",
                                            modifier = Modifier.fillMaxSize().background(Color.Transparent, CircleShape),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = "Icono por defecto",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                Column {
                                    Text(
                                        text = amigo.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = "Toca para abrir la conversación",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}