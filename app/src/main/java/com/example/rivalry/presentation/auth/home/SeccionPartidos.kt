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
import com.example.rivalry.presentation.auth.components.TarjetaPartido

@Composable
fun SeccionPartidos(viewModel: PartidoSueltoViewModel) {
    val misPartidos by viewModel.misPartidos.collectAsState()
    val partidosExplorar by viewModel.partidosExplorar.collectAsState()

    if (misPartidos.isEmpty() && partidosExplorar.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No hay pachangas activas", color = Color.Gray, fontWeight = FontWeight.Bold)
            Text("¡Anímate y organiza la primera!", fontSize = 14.sp, color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (misPartidos.isNotEmpty()) {
                item {
                    Text("Mis Pachangas", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(misPartidos) { partido ->
                    TarjetaPartido(partido = partido, esMio = true, viewModel = viewModel)
                }
            }

            if (partidosExplorar.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Buscar Partidos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(partidosExplorar) { partido ->
                    TarjetaPartido(partido = partido, esMio = false, viewModel = viewModel)
                }
            }
        }
    }
}