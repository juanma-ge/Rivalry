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
import com.example.rivalry.presentation.auth.components.ItemLiga
import com.example.rivalry.presentation.auth.home.LigaViewModel
import com.example.rivalry.presentation.auth.home.PartidoSueltoViewModel
import com.example.rivalry.presentation.auth.home.SeccionPartidos
import com.example.rivalry.presentation.auth.home.SeccionPerfil
import com.example.rivalry.presentation.auth.home.SeccionSocial
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHome(
    viewModel: LigaViewModel,
    partidoViewModel: PartidoSueltoViewModel,
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

    var navSeleccionada by remember { mutableStateOf(0) }
    var busquedaLiga by remember { mutableStateOf("") }

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
                    actions = {
                        IconButton(onClick = { /* TODO: Pantalla de Notificaciones */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Avisos",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
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
                    icon = { Icon(Icons.Default.Email, contentDescription = "Mensajes") }, // NUEVO ICONO SOCIAL
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
                                OutlinedTextField(
                                    value = busquedaLiga,
                                    onValueChange = { busquedaLiga = it },
                                    label = { Text("Buscar liga por nombre, ciudad o provincia") },
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
                                                "No hay ligas disponibles en '${busquedaLiga}'.",
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
                    SeccionPartidos(viewModel = partidoViewModel)
                }

                2 -> {
                    SeccionSocial()
                }

                3 -> {
                    SeccionPerfil(
                        ligas = misLigas,
                        partidos = emptyList(),
                        onLigaClick = { idLiga ->
                            onNavegarADetalleLiga(idLiga)
                        }
                    )
                }
            }
        }
    }
}