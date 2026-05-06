package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SeccionPartidos(viewModel: PartidoSueltoViewModel) {
    val partidos by viewModel.partidosExplorar.collectAsState()

    if (partidos.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay partidos disponibles", color = Color.Gray)
            Text("¡Pronto podrás crear el tuyo!", fontSize = 12.sp, color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(partidos) { partido ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = partido.deporte.uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${partido.idsJugadores.size}/${partido.maxJugadores} plazas",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("📅 ${partido.fecha} - 🕒 ${partido.hora}", fontSize = 14.sp)
                        Text("📍 ${partido.ubicacion} (${partido.nivel})", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.unirseAPartido(partido.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Apuntarse")
                        }
                    }
                }
            }
        }
    }
}