package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rivalry.domain.model.Partido
import com.example.rivalry.presentation.auth.components.CardPartidoMini
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SeccionPerfil(
    ligas: List<com.example.rivalry.domain.model.Liga>,
    partidos: List<Partido>,
    onLigaClick: (String) -> Unit,
    viewModel: PerfilViewModel = viewModel()
) {
    val user = FirebaseAuth.getInstance().currentUser
    val miId = user?.uid ?: ""
    val miCorreo = user?.email ?: "Usuario"
    val codigoAmigo = "RIV-${miId.take(5).uppercase()}"

    val perfilActual by viewModel.perfil.collectAsState()

    var apodo by remember { mutableStateOf("") }
    var posicion by remember { mutableStateOf("") }
    var dorsal by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.cargarPerfil() }
    LaunchedEffect(perfilActual) {
        apodo = perfilActual.apodo
        posicion = perfilActual.posicion
        dorsal = perfilActual.dorsal
        bio = perfilActual.bio
    }

    val nombreMostrar = if (perfilActual.apodo.isEmpty()) miCorreo else perfilActual.apodo

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Código amigo: $codigoAmigo",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { /* TODO: Ajustes */ }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        onClick = {
                            if (isEditing) viewModel.guardarPerfil(apodo, posicion, dorsal, bio)
                            isEditing = !isEditing
                        },
                        shape = CircleShape,
                        color = if (isEditing) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.3f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isEditing) Color.White else Color.DarkGray
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(90.dp).clip(CircleShape).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(45.dp), tint = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (isEditing) {
                    OutlinedTextField(
                        value = apodo,
                        onValueChange = { apodo = it },
                        label = { Text("Apodo") },
                        modifier = Modifier.fillMaxWidth(0.7f),
                        singleLine = true
                    )
                } else {
                    Text(text = nombreMostrar, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            if (isEditing) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = posicion, onValueChange = { posicion = it }, label = { Text("Posición") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = dorsal, onValueChange = { dorsal = it }, label = { Text("Dorsal") }, modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    InfoCaja("Posición", perfilActual.posicion)
                    InfoCaja("Dorsal", perfilActual.dorsal)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = perfilActual.bio,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text("Ligas que juega", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (ligas.isEmpty()) {
                Text("Aún no participa en ninguna liga", fontSize = 12.sp, color = Color.Gray)
            } else {
                LazyRow(contentPadding = PaddingValues(end = 16.dp)) {
                    items(ligas) { liga ->
                        Card(
                            onClick = { onLigaClick(liga.id) },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                                Text(text = liga.nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = liga.deporte, fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "Últimos partidos",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (partidos.isEmpty()) {
                Text(
                    text = "No constan partidos disputados en el historial.",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        if (partidos.isNotEmpty()) {
            items(partidos) { partido ->
                CardPartidoMini(partido = partido)
            }
        }
    }
}

@Composable
fun InfoCaja(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 11.sp)
        Text(if (value.isBlank()) "-" else value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}