package com.example.rivalry.presentation.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.rivalry.domain.model.AmigoUI
import com.example.rivalry.domain.model.SolicitudUI
import com.example.rivalry.presentation.auth.home.SocialViewModel

@Composable
fun ContenidoListaSocial(
    viewModel: SocialViewModel,
    codigoBusqueda: String,
    onCodigoChange: (String) -> Unit,
    textoFiltroAmigos: String,
    onFiltroChange: (String) -> Unit,
    mensajeUI: String?,
    solicitudes: List<SolicitudUI>,
    amigosLista: List<AmigoUI>,
    amigosFiltrados: List<AmigoUI>,
    onAmigoClick: (String, String, String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Añadir Amigo", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = codigoBusqueda,
                    onValueChange = onCodigoChange,
                    label = { Text("Ej: RIV-A1B2C") },
                    modifier = Modifier.weight(1f),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.buscarYEnviarSolicitud(codigoBusqueda)
                        onCodigoChange("")
                    },
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir")
                }
            }
        }

        mensajeUI?.let { mensaje ->
            item {
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
        }

        if (solicitudes.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Solicitudes Pendientes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
            }

            items(solicitudes) { solicitud ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
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

        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Mensajes Directos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        item {
            OutlinedTextField(
                value = textoFiltroAmigos,
                onValueChange = onFiltroChange,
                placeholder = { Text("Buscar entre tus amigos...") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Lupa amigos", tint = Color.Gray) },
                singleLine = true
            )
        }

        if (amigosLista.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes amigos.\n¡Busca a un amigo por su código!", color = Color.Gray, textAlign = TextAlign.Center, fontSize = 14.sp)
                }
            }
        } else if (amigosFiltrados.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text("No se encontraron coincidencias.", color = Color.Gray, textAlign = TextAlign.Center)
                }
            }
        } else {
            items(amigosFiltrados) { amigo ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable {
                        viewModel.obtenerOCrearChatPrivado(amigo.id) { idChatGenerado ->
                            onAmigoClick(idChatGenerado, amigo.nombre, amigo.avatarUrl)
                        }
                    },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
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
                                Icon(Icons.Default.Person, contentDescription = "Icono por defecto", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(amigo.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Toca para chatear", color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}