package com.example.rivalry.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.presentation.auth.components.ItemLiga
import com.example.rivalry.presentation.auth.home.LigaViewModel
import com.example.rivalry.presentation.auth.home.PartidoSueltoViewModel
import com.example.rivalry.presentation.auth.home.SeccionPartidos
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material.icons.filled.Search

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

    var pestaniaSeleccionada by remember { mutableStateOf(0) }
    val titulosPestanias = listOf("Mis ligas", "Explorar")

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
                            2 -> "AVISOS"
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
                    TabRow(selectedTabIndex = pestaniaSeleccionada) {
                        titulosPestanias.forEachIndexed { index, titulo ->
                            Tab(
                                selected = pestaniaSeleccionada == index,
                                onClick = { pestaniaSeleccionada = index },
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
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Avisos") },
                    label = { Text("Avisos") },
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
                    if (pestaniaSeleccionada == 0) {
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

                1 -> {
                    SeccionPartidos(viewModel = partidoViewModel)
                }

                2 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No tienes notificaciones nuevas", color = Color.Gray)
                    }
                }

                3 -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(100.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Ajustes de Perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Text(FirebaseAuth.getInstance().currentUser?.email ?: "Usuario", color = Color.Gray)

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                onCerrarSesion()
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}