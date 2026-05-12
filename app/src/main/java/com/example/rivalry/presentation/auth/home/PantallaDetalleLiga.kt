package com.example.rivalry.presentation.auth.home

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
import com.example.rivalry.presentation.auth.chat.ChatViewModel
import com.example.rivalry.presentation.auth.chat.PestaniaEquipos
import com.example.rivalry.presentation.auth.chat.PestaniaFichajes
import com.example.rivalry.presentation.auth.chat.PestaniaPartidos
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalleLiga(
    ligaId: String,
    viewModel: LigaViewModel,
    onVolver: () -> Unit
) {
    val pestanias = listOf("Partidos", "Clasificación", "Info", "Equipos", "Fichajes", "Chat")
    val pagerState = rememberPagerState(pageCount = { pestanias.size })
    val coroutineScope = rememberCoroutineScope()

    val liga by viewModel.ligaSeleccionada.collectAsState()
    val miembrosLista by viewModel.miembrosLiga.collectAsState()
    val agentesLista by viewModel.agentesLibres.collectAsState()
    val chatViewModel: ChatViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    val miId = FirebaseAuth.getInstance().currentUser?.uid
    val estoyApuntado = liga?.idsMiembros?.contains(miId) == true
    val soyAgente = liga?.idsAgentesLibres?.contains(miId) == true

    val esAdmin = liga?.creadorId == miId
    val partidosLista by viewModel.partidosLiga.collectAsState()

    var mostrarDialogoUnirse by remember { mutableStateOf(false) }
    var pasoDialogo by remember { mutableStateOf(1) }
    var nombreEquipoInput by remember { mutableStateOf("") }

    LaunchedEffect(ligaId) {
        viewModel.cargarDetalleLiga(ligaId)
        viewModel.cargarPartidosLiga(ligaId)
    }

    LaunchedEffect(liga) {
        liga?.let {
            viewModel.cargarMiembros(it.idsMiembros, it.creadorId, it.nombresEquipos)
            viewModel.cargarAgentesLibres(it.idsAgentesLibres)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Liga", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White, navigationIconContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(liga?.nombre ?: "Cargando...", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color.LightGray), contentAlignment = Alignment.Center) {
                    Text("LOGO", color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("${liga?.deporte} • ${liga?.ciudad}", color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                if (liga != null) {
                    val btnModifier = Modifier.width(200.dp)
                    if (estoyApuntado) {
                        OutlinedButton(onClick = { viewModel.salirDeLiga(ligaId, onVolver) }, btnModifier) { Text("Retirar Equipo", color = Color.Red) }
                    } else if (soyAgente) {
                        OutlinedButton(onClick = { viewModel.salirDeAgentesLibres(ligaId) }, btnModifier) { Text("Quitar Anuncio", color = Color.Red) }
                    } else {
                        Button(onClick = { mostrarDialogoUnirse = true }, btnModifier) { Text("Unirme a la Liga") }
                    }
                }
            }

            ScrollableTabRow(selectedTabIndex = pagerState.currentPage, edgePadding = 16.dp) {
                pestanias.forEachIndexed { i, t ->
                    Tab(selected = pagerState.currentPage == i, onClick = { coroutineScope.launch { pagerState.animateScrollToPage(i) } }, text = { Text(t) })
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> PestaniaPartidos(
                        partidos = partidosLista,
                        esAdmin = esAdmin,
                        onAbrirFormulario = {
                            println("Click en crear partido")
                        }
                    )
                    1 -> Text("Aquí irá la tabla de clasificación.", modifier = Modifier.padding(16.dp))
                    2 -> Text("Información general y normas.", modifier = Modifier.padding(16.dp))
                    3 -> PestaniaEquipos(liga, miembrosLista)
                    4 -> PestaniaFichajes(agentesLista, liga?.nombre ?: "")
                    5 -> if (estoyApuntado) {
                        ChatIntegrado(ligaId, chatViewModel, miId)
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Únete para chatear", color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (mostrarDialogoUnirse) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoUnirse = false; pasoDialogo = 1 },
                title = { Text(if (pasoDialogo == 1) "¿Cómo quieres unirte?" else "Nombre del equipo") },
                text = {
                    if (pasoDialogo == 1) {
                        Column {
                            Button(onClick = { pasoDialogo = 2 }, Modifier.fillMaxWidth()) { Text("Ser Capitán (Inscribir Equipo)") }
                            OutlinedButton(onClick = { viewModel.apuntarseComoAgenteLibre(ligaId); mostrarDialogoUnirse = false }, Modifier.fillMaxWidth()) { Text("Ser Agente (Busco Equipo)") }
                        }
                    } else {
                        OutlinedTextField(value = nombreEquipoInput, onValueChange = { nombreEquipoInput = it }, label = { Text("Nombre del equipo") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    if (pasoDialogo == 2) {
                        Button(onClick = {
                            if (nombreEquipoInput.isNotBlank()) {
                                viewModel.unirseALiga(ligaId, nombreEquipoInput)
                                mostrarDialogoUnirse = false
                            }
                        }) { Text("Confirmar") }
                    }
                }
            )
        }
    }
}