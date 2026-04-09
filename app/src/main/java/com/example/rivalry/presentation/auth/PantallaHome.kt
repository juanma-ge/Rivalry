package com.example.rivalry.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Deporte
import com.example.rivalry.presentation.auth.components.ItemLiga
import com.example.rivalry.presentation.auth.home.LigaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaHome(viewModel: LigaViewModel, onNavegarACrearLiga: () -> Unit)  {

    val misLigas by viewModel.misLigas.collectAsState()
    val ligasExplorar by viewModel.ligasExplorar.collectAsState()

    var pestaniaSeleccionada by remember { mutableStateOf(0) }
    val titulosPestanias = listOf("Mis ligas", "Explorar")
    var navSeleccionada by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("RIVALRY", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
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
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    selected = navSeleccionada == 0,
                    onClick = { navSeleccionada = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Notifications, contentDescription = "Avisos") },
                    label = { Text("Avisos") },
                    selected = navSeleccionada == 1,
                    onClick = { navSeleccionada = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil") },
                    selected = navSeleccionada == 2,
                    onClick = { navSeleccionada = 2 }
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
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
                            esPublica = liga.esPublica
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text("Ligas buscando equipos", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    items(ligasExplorar) { liga ->
                        ItemLiga(
                            nombre = liga.nombre,
                            deporte = liga.deporte,
                            participantes = liga.idsMiembros.size,
                            maxParticipantes = liga.maxParticipantes,
                            esPublica = liga.esPublica
                        )
                    }
                }
            }
        }
    }
}