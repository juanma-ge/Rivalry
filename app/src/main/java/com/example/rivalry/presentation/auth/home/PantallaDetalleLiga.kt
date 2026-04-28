package com.example.rivalry.presentation.home

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
import com.example.rivalry.presentation.auth.home.LigaViewModel
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleLiga(
    ligaId: String,
    viewModel: LigaViewModel,
    onVolver: () -> Unit
) {
    val pestanias = listOf("Partidos", "Clasificación", "Info", "Equipos", "Fichajes", "Noticias")
    var pestaniaSeleccionada by remember { mutableStateOf(0) }

    val liga by viewModel.ligaSeleccionada.collectAsState()

    LaunchedEffect(ligaId) {
        viewModel.cargarDetalleLiga(ligaId)
    }

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
                Text(
                    text = liga?.nombre ?: "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                val miId = FirebaseAuth.getInstance().currentUser?.uid
                val estoyApuntado = liga?.idsMiembros?.contains(miId) == true
                val plazasOcupadas = liga?.idsMiembros?.size ?: 0
                val plazasTotales = liga?.maxParticipantes ?: 0

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (liga != null) {
                        if (estoyApuntado) {
                            OutlinedButton(onClick = { viewModel.salirDeLiga(ligaId) }) {
                                Text("Salir", color = Color.Red)
                            }
                        } else {
                            if (plazasOcupadas < plazasTotales) {
                                Button(onClick = { viewModel.unirseALiga(ligaId) }) {
                                    Text("Unirme")
                                }
                            } else {
                                Text("Llena", color = Color.Red, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Logo", color = Color.DarkGray)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Spacer(modifier = Modifier.width(80.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = liga?.deporte ?: "",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            ScrollableTabRow(
                selectedTabIndex = pestaniaSeleccionada,
                edgePadding = 8.dp,
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
                    0 -> Text("Aquí irán los resultados de los partidos (Jornada a jornada).")
                    1 -> Text("Aquí irá la tabla de clasificación con puntos, goles, etc.")
                    2 -> Text("Información general: Normas, administrador, etc.")

                    3 -> {
                        val plazasOcupadas = liga?.idsMiembros?.size ?: 0
                        val plazasTotales = liga?.maxParticipantes ?: 0
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Participantes: $plazasOcupadas / $plazasTotales",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Aquí irá la lista de nombres de los equipos.")
                        }
                    }

                    4 -> Text("Mercado de fichajes.")
                    5 -> Text("Muro de noticias o avisos del administrador.")
                }
            }
        }
    }
}