package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleLiga(ligaId: String, onVolver: () -> Unit) {

    val pestanias = listOf("Partidos", "Clasificación", "Info", "Equipos", "Fichajes", "Noticias")
    var pestaniaSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Competición", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onVolver() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Cargando nombre...", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Logo", color = Color.DarkGray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "ID: $ligaId", color = Color.Gray, fontSize = 12.sp)
            }

            ScrollableTabRow(
                selectedTabIndex = pestaniaSeleccionada,
                edgePadding = 8.dp, // Espacio a los lados para que no empiece pegado al borde
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                pestanias.forEachIndexed { index, titulo ->
                    Tab(
                        selected = pestaniaSeleccionada == index,
                        onClick = { pestaniaSeleccionada = index },
                        text = {
                            Text(
                                text = titulo,
                                fontWeight = if (pestaniaSeleccionada == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (pestaniaSeleccionada) {
                    0 -> Text("⚽ Aquí irán los resultados de los partidos (Jornada a jornada).")
                    1 -> Text("🏆 Aquí irá la tabla de clasificación con puntos, goles, etc.")
                    2 -> Text("ℹ️ Información general: Normas, administrador, etc.")
                    3 -> Text("🛡️ Lista de equipos y jugadores inscritos.")
                    4 -> Text("🤝 Mercado de fichajes.")
                    5 -> Text("📰 Muro de noticias o avisos del administrador.")
                }
            }
        }
    }
}