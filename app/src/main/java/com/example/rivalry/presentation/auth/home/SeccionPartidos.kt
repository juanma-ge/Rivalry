package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Partido
import com.example.rivalry.domain.model.PartidoSuelto
import com.example.rivalry.presentation.auth.components.CardPartidoLiga
import com.example.rivalry.presentation.auth.components.TarjetaPartido
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionPartidos(
    viewModel: PartidoSueltoViewModel,
    partidosLiga: List<Partido> = emptyList(),
    misPartidosSueltos: List<PartidoSuelto> = emptyList(),
    partidosExplorar: List<PartidoSuelto> = emptyList(),
    onVerClasificacionClick: (String) -> Unit = {}
) {
    val titulosPrincipales = listOf("Mis Partidos", "Explorar")
    val pagerStatePrincipal = rememberPagerState(pageCount = { titulosPrincipales.size })
    val coroutineScope = rememberCoroutineScope()
    var busquedaPartido by remember { mutableStateOf("") }

    val titulosSubPestanias = listOf("Próximos", "Jugados")
    val pagerStateSub = rememberPagerState(pageCount = { titulosSubPestanias.size })

    val proximosLiga = partidosLiga.filter { !it.terminado && it.estado != "FINALIZADO" }
    val historialLiga = partidosLiga.filter { it.terminado || it.estado == "FINALIZADO" }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pagerStatePrincipal.currentPage) {
            titulosPrincipales.forEachIndexed { index, titulo ->
                Tab(
                    selected = pagerStatePrincipal.currentPage == index,
                    onClick = { coroutineScope.launch { pagerStatePrincipal.animateScrollToPage(index) } },
                    text = { Text(titulo, fontSize = 15.sp, fontWeight = FontWeight.Medium) }
                )
            }
        }

        HorizontalPager(state = pagerStatePrincipal, modifier = Modifier.fillMaxSize()) { pagePrincipal ->
            if (pagePrincipal == 0) {
                Column(modifier = Modifier.fillMaxSize()) {

                    TabRow(
                        selectedTabIndex = pagerStateSub.currentPage,
                        containerColor = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    ) {
                        titulosSubPestanias.forEachIndexed { index, titulo ->
                            Tab(
                                selected = pagerStateSub.currentPage == index,
                                onClick = { coroutineScope.launch { pagerStateSub.animateScrollToPage(index) } },
                                text = { Text(titulo, fontSize = 14.sp) }
                            )
                        }
                    }

                    HorizontalPager(state = pagerStateSub, modifier = Modifier.weight(1f)) { pageSub ->
                        when (pageSub) {
                            0 -> {
                                LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
                                    if (proximosLiga.isEmpty() && misPartidosSueltos.isEmpty()) {
                                        item {
                                            Text("No tienes encuentros programados.", color = Color.Gray, fontSize = 13.sp)
                                        }
                                    } else {
                                        items(proximosLiga) { partido ->
                                            CardPartidoLiga(
                                                partido = partido,
                                                onVerClasificacionClick = { onVerClasificacionClick(partido.idLiga) }
                                            )
                                        }
                                        items(misPartidosSueltos) { pachanga ->
                                            TarjetaPartido(partido = pachanga, esMio = true, viewModel = viewModel)
                                        }
                                    }
                                }
                            }
                            1 -> {
                                LazyColumn(contentPadding = PaddingValues(16.dp), modifier = Modifier.fillMaxSize()) {
                                    if (historialLiga.isEmpty()) {
                                        item {
                                            Text("Aún no constan partidos finalizados.", color = Color.Gray, fontSize = 13.sp)
                                        }
                                    } else {
                                        items(historialLiga) { partido ->
                                            CardPartidoLiga(
                                                partido = partido,
                                                onVerClasificacionClick = { onVerClasificacionClick(partido.idLiga) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    OutlinedTextField(
                        value = busquedaPartido,
                        onValueChange = { busquedaPartido = it },
                        label = { Text("Buscar por ciudad o provincia") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        val filtrados = if (busquedaPartido.isBlank()) partidosExplorar else {
                            partidosExplorar.filter {
                                it.ciudad.contains(busquedaPartido, ignoreCase = true) ||
                                        it.provincia.contains(busquedaPartido, ignoreCase = true)
                            }
                        }

                        if (filtrados.isEmpty()) {
                            item { Text("No hay pachangas disponibles en esa zona.", color = Color.Gray, fontSize = 14.sp) }
                        } else {
                            items(filtrados) { partido ->
                                TarjetaPartido(partido = partido, esMio = false, viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}