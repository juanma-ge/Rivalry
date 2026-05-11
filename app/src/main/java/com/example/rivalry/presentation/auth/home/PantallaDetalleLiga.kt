package com.example.rivalry.presentation.auth.home

import com.example.rivalry.presentation.auth.chat.ChatViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import com.example.rivalry.presentation.auth.chat.ChatIntegrado
import com.example.rivalry.presentation.auth.chat.PestaniaEquipos

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleLiga(
    ligaId: String,
    viewModel: LigaViewModel,
    onVolver: () -> Unit,
    onNavegarAChat: (String, String) -> Unit
) {
    val pestanias = listOf("Partidos", "Clasificación", "Info", "Equipos", "Fichajes", "Chat")

    val pagerState = rememberPagerState(pageCount = { pestanias.size })
    val coroutineScope = rememberCoroutineScope()

    val liga by viewModel.ligaSeleccionada.collectAsState()
    val miembrosLista by viewModel.miembrosLiga.collectAsState()
    val chatViewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    LaunchedEffect(liga?.idsMiembros) {
        liga?.let { l -> viewModel.cargarMiembros(l.idsMiembros, l.creadorId) }
    }

    LaunchedEffect(ligaId) {
        viewModel.cargarDetalleLiga(ligaId)
    }

    val miId = FirebaseAuth.getInstance().currentUser?.uid
    val estoyApuntado = liga?.idsMiembros?.contains(miId) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Competición", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onVolver() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = liga?.nombre ?: "Cargando...", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                val plazasOcupadas = liga?.idsMiembros?.size ?: 0
                val plazasTotales = if ((liga?.maxParticipantes ?: 0) > 0) liga!!.maxParticipantes else 20

                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.align(Alignment.CenterStart)) {
                        if (liga != null) {
                            if (estoyApuntado) {
                                OutlinedButton(onClick = { viewModel.salirDeLiga(ligaId) { onVolver() } }) { Text("Salir", color = Color.Red) }
                            } else {
                                if (plazasOcupadas < plazasTotales) {
                                    Button(onClick = { viewModel.unirseALiga(ligaId) }) { Text("Unirme") }
                                } else {
                                    Text("Llena", color = Color.Red, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.LightGray).align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) { Text("Logo", color = Color.DarkGray) }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "${liga?.deporte ?: ""} • ${liga?.ciudad ?: ""}", color = Color.Gray, fontSize = 14.sp)
            }

            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                edgePadding = 8.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                pestanias.forEachIndexed { index, titulo ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(text = titulo, fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> Text("Aquí irán los resultados.", modifier = Modifier.padding(16.dp))
                    1 -> Text("Aquí irá la tabla de clasificación.", modifier = Modifier.padding(16.dp))
                    2 -> Text("Información general y normas.", modifier = Modifier.padding(16.dp))
                    3 -> PestaniaEquipos(liga = liga, miembrosLista = miembrosLista)
                    4 -> Text("Muro automático de fichajes (Próximamente).", modifier = Modifier.padding(16.dp))
                    5 -> {
                        if (estoyApuntado) {
                            ChatIntegrado(salaId = ligaId, viewModel = chatViewModel, miId = miId)
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Debes unirte a la liga para usar el chat.", color = Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}