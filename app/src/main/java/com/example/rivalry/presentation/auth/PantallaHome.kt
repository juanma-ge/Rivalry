package com.example.rivalry.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rivalry.presentation.auth.components.ItemLiga
import com.example.rivalry.presentation.auth.home.LigaViewModel
import com.example.rivalry.presentation.auth.home.PartidoSueltoViewModel
import com.example.rivalry.presentation.auth.home.PerfilViewModel
import com.example.rivalry.presentation.auth.home.SocialViewModel
import com.example.rivalry.presentation.auth.home.SeccionPartidos
import com.example.rivalry.presentation.auth.home.SeccionPerfil
import com.example.rivalry.presentation.auth.home.SeccionSocial
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHome(
    viewModel: LigaViewModel,
    partidoViewModel: PartidoSueltoViewModel,
    socialViewModel: SocialViewModel = viewModel(),
    perfilViewModel: PerfilViewModel = viewModel(),
    onNavegarACrearLiga: () -> Unit,
    onNavegarADetalleLiga: (String) -> Unit,
    onNavegarACrearPartido: () -> Unit,
    onCerrarSesion: () -> Unit
) {

    val misLigas by viewModel.misLigas.collectAsState()
    val ligasExplorar by viewModel.ligasExplorar.collectAsState()

    val titulosPestanias = listOf("Mis ligas", "Explorar")

    val pagerState = rememberPagerState(pageCount = { titulosPestanias.size })
    val coroutineScope = rememberCoroutineScope()

    var navSeleccionada by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(0) }
    var busquedaLiga by remember { mutableStateOf("") }
    val partidosLiga by partidoViewModel.partidosLiga.collectAsState()
    val misPartidosSueltos by partidoViewModel.misPartidosSueltos.collectAsState()
    val partidosExplorar by partidoViewModel.partidosExplorar.collectAsState()

    // --- VARIABLES PARA EL CÓDIGO DE LIGA PRIVADA ---
    var mostrarDialogoCodigo by remember { mutableStateOf(false) }
    var codigoInput by remember { mutableStateOf("") }
    var nombreEquipoCodigoInput by remember { mutableStateOf("") }
    var mensajeErrorCodigo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        val titulo = when(navSeleccionada) {
                            0 -> "RIVALRY LIGAS"
                            1 -> "PARTIDOS"
                            2 -> "MENSAJES"
                            else -> "MI PERFIL"
                        }
                        Text(titulo, fontWeight = FontWeight.Bold)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )

                if (navSeleccionada == 0) {
                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        titulosPestanias.forEachIndexed { index, titulo ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                },
                                text = { Text(titulo, fontSize = 16.sp) }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Ligas") },
                    label = { Text("Ligas") },
                    selected = navSeleccionada == 0,
                    onClick = { navSeleccionada = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Partidos") },
                    label = { Text("Partidos") },
                    selected = navSeleccionada == 1,
                    onClick = { navSeleccionada = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Email, contentDescription = "Mensajes") },
                    label = { Text("Mensajes") },
                    selected = navSeleccionada == 2,
                    onClick = { navSeleccionada = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = navSeleccionada == 3,
                    onClick = { navSeleccionada = 3 }
                )
            }
        },
        floatingActionButton = {
            if (navSeleccionada == 0) {
                FloatingActionButton(
                    onClick = { onNavegarACrearLiga() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Liga")
                }
            } else if (navSeleccionada == 1) {
                FloatingActionButton(
                    onClick = { onNavegarACrearPartido() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Partido")
                }
            }
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (navSeleccionada) {
                0 -> {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        if (page == 0) {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item {
                                    Text("Tus competiciones activas", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                                }
                                items(misLigas) { liga ->
                                    ItemLiga(
                                        nombre = liga.nombre,
                                        deporte = liga.deporte,
                                        participantes = liga.idsMiembros.size,
                                        maxParticipantes = liga.maxParticipantes,
                                        esPublica = liga.esPublica,
                                        onClick = {
                                            if (liga.id.isNotBlank()) {
                                                onNavegarADetalleLiga(liga.id)
                                            }
                                        }
                                    )
                                }
                            }
                        } else {
                            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                                Button(
                                    onClick = { mostrarDialogoCodigo = true },
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.secondary
                                    )
                                ) {
                                    Text("Tengo un código de Liga Privada", fontWeight = FontWeight.Bold)
                                }

                                OutlinedTextField(
                                    value = busquedaLiga,
                                    onValueChange = { busquedaLiga = it },
                                    label = { Text("Buscar liga pública por nombre o ciudad") },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                                    singleLine = true
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    val ligasFiltradas = if (busquedaLiga.isBlank()) {
                                        ligasExplorar
                                    } else {
                                        ligasExplorar.filter {
                                            it.ciudad.contains(busquedaLiga, ignoreCase = true) ||
                                                    it.provincia.contains(busquedaLiga, ignoreCase = true) ||
                                                    it.nombre.contains(busquedaLiga, ignoreCase = true)
                                        }
                                    }

                                    if (ligasFiltradas.isEmpty()) {
                                        item {
                                            Text(
                                                "No hay ligas públicas disponibles en '${busquedaLiga}'.",
                                                color = Color.Gray,
                                                modifier = Modifier.padding(top = 16.dp)
                                            )
                                        }
                                    } else {
                                        items(ligasFiltradas) { liga ->
                                            ItemLiga(
                                                nombre = liga.nombre,
                                                deporte = liga.deporte,
                                                participantes = liga.idsMiembros.size,
                                                maxParticipantes = liga.maxParticipantes,
                                                esPublica = liga.esPublica,
                                                onClick = {
                                                    if (liga.id.isNotBlank()) {
                                                        onNavegarADetalleLiga(liga.id)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    SeccionPartidos(
                        viewModel = partidoViewModel,
                        partidosLiga = partidosLiga,
                        misPartidosSueltos = misPartidosSueltos,
                        partidosExplorar = partidosExplorar,
                        onVerClasificacionClick = { idLiga ->
                            onNavegarADetalleLiga(idLiga)
                        }
                    )
                }
                2 -> {
                    SeccionSocial(viewModel = socialViewModel)
                }

                3 -> {
                    val misPartidosFinalizadosLiga = partidosLiga.filter { it.terminado || it.estado == "FINALIZADO" }
                    SeccionPerfil(
                        viewModel = perfilViewModel,
                        ligas = misLigas,
                        partidos = misPartidosFinalizadosLiga,
                        onLigaClick = { idLiga ->
                            onNavegarADetalleLiga(idLiga)
                        },
                        onLogout = {
                            com.google.firebase.auth.FirebaseAuth.getInstance().signOut()
                            onCerrarSesion()
                        }
                    )
                }
            }

              if (mostrarDialogoCodigo) {
                AlertDialog(
                    onDismissRequest = { mostrarDialogoCodigo = false },
                    title = { Text("Unirse a Liga Privada", fontWeight = FontWeight.Bold) },
                    text = {
                        Column {
                            Text("Pídele el código al administrador de la liga privada e introdúcelo aquí.", fontSize = 14.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = codigoInput,
                                onValueChange = { codigoInput = it.uppercase() },
                                label = { Text("Código (ej. RIV-A8F2)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = nombreEquipoCodigoInput,
                                onValueChange = { nombreEquipoCodigoInput = it },
                                label = { Text("Nombre de equipo (Opcional)") },
                                placeholder = { Text("Si lo dejas en blanco, serás Agente Libre") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            if (mensajeErrorCodigo.isNotEmpty()) {
                                Text(mensajeErrorCodigo, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (codigoInput.isNotBlank()) {
                                viewModel.unirseConCodigo(codigoInput.trim(), nombreEquipoCodigoInput.trim()) { exito, mensaje ->
                                    if (exito) {
                                        mostrarDialogoCodigo = false
                                        codigoInput = ""
                                        nombreEquipoCodigoInput = ""
                                        mensajeErrorCodigo = ""
                                    } else {
                                        mensajeErrorCodigo = mensaje
                                    }
                                }
                            }
                        }) { Text("Unirse") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarDialogoCodigo = false; mensajeErrorCodigo = "" }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}