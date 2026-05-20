package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
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
import com.example.rivalry.domain.model.Partido
import com.example.rivalry.presentation.auth.chat.ChatIntegrado
import com.example.rivalry.presentation.auth.chat.ChatViewModel
import com.example.rivalry.presentation.auth.chat.PestaniaEquipos
import com.example.rivalry.presentation.auth.chat.PestaniaFichajes
import com.example.rivalry.presentation.auth.chat.PantallaChatPrivado
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
    val noticiasLiga by viewModel.noticiasLiga.collectAsState()

    val miId = FirebaseAuth.getInstance().currentUser?.uid
    val estoyApuntado = liga?.idsMiembros?.contains(miId) == true
    val soyAgente = liga?.idsAgentesLibres?.contains(miId) == true

    val esAdmin = liga?.creadorId == miId
    val partidosLista by viewModel.partidosLiga.collectAsState()

    var mostrarDialogoUnirse by remember { mutableStateOf(false) }
    var pasoDialogo by remember { mutableStateOf(1) }
    var nombreEquipoInput by remember { mutableStateOf("") }
    var partidoSeleccionadoParaResultado by remember { mutableStateOf<Partido?>(null) }
    var golesLocalInput by remember { mutableStateOf("0") }
    var golesVisitanteInput by remember { mutableStateOf("0") }

    val agentesLibresUI by viewModel.agentesLibres.collectAsState()
    val ligaActual by viewModel.ligaSeleccionada.collectAsState()
    val miUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid

    var chatPrivadoActivoId by remember { mutableStateOf<String?>(null) }
    val socialViewModel: SocialViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    LaunchedEffect(ligaId) {
        viewModel.cargarDetalleLiga(ligaId)
        viewModel.cargarPartidosLiga(ligaId)
        viewModel.cargarNoticias(ligaId)
    }

    LaunchedEffect(liga) {
        liga?.let {
            viewModel.cargarMiembros(ligaId, it.idsMiembros, it.creadorId, it.nombresEquipos)
            viewModel.cargarAgentesLibres(it.idsAgentesLibres)
        }
    }

    LaunchedEffect(ligaActual) {
        ligaActual?.let {
            viewModel.cargarAgentesLibres(it.idsAgentesLibres)
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Liga", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    // BOTÓN PDF
                    IconButton(onClick = {
                        GeneradorPDF.generarYCompartir(
                            context = context,
                            nombreLiga = liga?.nombre ?: "Liga",
                            miembros = miembrosLista,
                            partidos = partidosLista
                        )
                    }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Share,
                            contentDescription = "Exportar PDF",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
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
                    0 -> {
                        if (partidosLista.isEmpty() && esAdmin) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("Aún no hay calendario.", color = Color.Gray)
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = { viewModel.generarCalendario(ligaId, miembrosLista) },
                                    modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
                                ) {
                                    Text("Generar Calendario Automático", fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            PestaniaPartidos(
                                partidos = partidosLista,
                                esAdmin = esAdmin,
                                onAbrirFormulario = { },
                                onPartidoClick = { partido ->
                                    if (esAdmin && partido.estado == "PENDIENTE") {
                                        partidoSeleccionadoParaResultado = partido
                                        golesLocalInput = "0"
                                        golesVisitanteInput = "0"
                                    }
                                }
                            )
                        }
                    }
                    1 -> PestaniaClasificacion(miembros = miembrosLista, partidos = partidosLista)
                    2 -> {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Text("Últimos Movimientos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(16.dp))

                            if (noticiasLiga.isEmpty()) {
                                Text("No hay noticias ni fichajes recientes.", color = Color.Gray)
                            } else {
                                LazyColumn {
                                    items(noticiasLiga) { noticia ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                        ) {
                                            Text(
                                                text = noticia.texto,
                                                modifier = Modifier.padding(16.dp),
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }                    3 -> PestaniaEquipos(
                        liga = liga,
                        miembros = miembrosLista,
                        miId = miId,
                        esAdmin = esAdmin,
                        onCambiarNombre = { idEquipo, nuevoNombre ->
                            viewModel.cambiarNombreEquipo(ligaId, idEquipo, nuevoNombre)
                        }
                    )
                    4 -> PestaniaFichajes(
                        agentesLibres = agentesLista,   
                        esCapitan = estoyApuntado,
                        onFicharJugador = { agente ->
                            val miNombreEquipo = liga?.nombresEquipos?.get(miId) ?: "Tu equipo"
                            viewModel.ficharAgenteLibre(ligaId, agente, miNombreEquipo)
                        },
                        onMensajeClick = { idAgente ->
                            socialViewModel.obtenerOCrearChatPrivado(idAgente) { idChatGenerado ->
                                chatPrivadoActivoId = idChatGenerado
                            }
                        }
                    )
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
                            if (liga?.estado == "EN_JUEGO") {
                                Text(
                                    text = "⚠️ La liga ya ha empezado. Podrás unirte y usar el chat, pero no jugarás hasta la próxima temporada.",
                                    color = Color.Red,
                                    fontSize = 13.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

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

        if (partidoSeleccionadoParaResultado != null) {
            AlertDialog(
                onDismissRequest = { partidoSeleccionadoParaResultado = null },
                title = { Text("Finalizar Partido", fontWeight = FontWeight.Bold) },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Introduce el resultado final", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(partidoSeleccionadoParaResultado!!.nombreLocal, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = golesLocalInput,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) golesLocalInput = it },
                                    modifier = Modifier.width(60.dp)
                                )
                            }
                            Text("-", fontSize = 24.sp, modifier = Modifier.padding(horizontal = 8.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                                Text(partidoSeleccionadoParaResultado!!.nombreVisitante, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                                OutlinedTextField(
                                    value = golesVisitanteInput,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) golesVisitanteInput = it },
                                    modifier = Modifier.width(60.dp)
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.finalizarPartido(
                            partidoId = partidoSeleccionadoParaResultado!!.id,
                            golesL = golesLocalInput.toIntOrNull() ?: 0,
                            golesV = golesVisitanteInput.toIntOrNull() ?: 0,
                            goleadoresMapa = emptyMap()
                        )
                        partidoSeleccionadoParaResultado = null
                    }) { Text("Guardar Resultado") }
                },
                dismissButton = {
                    TextButton(onClick = { partidoSeleccionadoParaResultado = null }) { Text("Cancelar") }
                }
            )
        }
    }

    chatPrivadoActivoId?.let { idDelChat ->
        PantallaChatPrivado(
            idChat = idDelChat,
            onVolver = { chatPrivadoActivoId = null }
        )
    }

}