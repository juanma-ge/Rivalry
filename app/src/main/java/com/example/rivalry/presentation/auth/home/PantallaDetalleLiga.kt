package com.example.rivalry.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
    val miembros by viewModel.miembrosLiga.collectAsState()

    LaunchedEffect(liga?.idsMiembros) {
        liga?.let { l ->
            viewModel.cargarMiembros(l.idsMiembros, l.creadorId)
        }
    }

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
            // --- CABECERA DE LA LIGA ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = liga?.nombre ?: "Cargando...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                val miId = FirebaseAuth.getInstance().currentUser?.uid
                val soyCreador = liga?.creadorId == miId
                val estoyApuntado = liga?.idsMiembros?.contains(miId) == true
                val plazasOcupadas = liga?.idsMiembros?.size ?: 0
                val plazasTotales = if ((liga?.maxParticipantes ?: 0) > 0) liga!!.maxParticipantes else 20

                Box(modifier = Modifier.fillMaxWidth()) {

                    Box(modifier = Modifier.align(Alignment.CenterStart)) {
                        if (liga != null) {
                            if (estoyApuntado) {
                                OutlinedButton(onClick = { viewModel.salirDeLiga(ligaId) { onVolver() } }) {
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
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Logo", color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                        val miembros by viewModel.miembrosLiga.collectAsState()

                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text(
                                text = "Participantes: ${liga?.idsMiembros?.size ?: 0} / ${liga?.maxParticipantes ?: 0}",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            if (miembros.isEmpty()) {
                                Text(
                                    "Cargando lista de jugadores...",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    color = Color.Gray
                                )
                            } else {
                                androidx.compose.foundation.lazy.LazyColumn {
                                    items(miembros.size) { index ->
                                        val miembro = miembros[index]
                                        Card(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(text = miembro.nombre, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)

                                                if (miembro.esAdmin) {
                                                    Text("👑 ADMIN", color = Color(0xFFDAA520), fontWeight = FontWeight.Black, fontSize = 11.sp)
                                                } else {
                                                    Text("Jugador", color = Color.Gray, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> Text("Mercado de fichajes.")
                    5 -> Text("Muro de noticias o avisos del administrador.")
                }
            }
        }
    }
}