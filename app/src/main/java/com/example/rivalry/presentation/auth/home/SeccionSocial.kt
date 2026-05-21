package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.rivalry.presentation.auth.chat.PantallaChatPrivado
import com.example.rivalry.presentation.auth.components.ContenidoListaSocial
import androidx.activity.compose.BackHandler

@Composable
fun SeccionSocial(viewModel: SocialViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val configuracion = androidx.compose.ui.platform.LocalConfiguration.current
    val esPantallaAncha = configuracion.screenWidthDp > 600

    var codigoBusqueda by remember { mutableStateOf("") }
    var textoFiltroAmigos by remember { mutableStateOf("") }
    val mensajeUI by viewModel.mensajeUI.collectAsState()
    val solicitudes by viewModel.solicitudes.collectAsState()
    val amigosLista by viewModel.amigosLista.collectAsState()

    var idChatActivoId by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }
    var nombreChatActivo by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }
    var avatarChatActivo by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }

    val amigosFiltrados = remember(amigosLista, textoFiltroAmigos) {
        if (textoFiltroAmigos.isBlank()) amigosLista
        else amigosLista.filter { it.nombre.contains(textoFiltroAmigos, ignoreCase = true) }
    }

    BackHandler(enabled = idChatActivoId != null) {
        idChatActivoId = null
    }

    if (esPantallaAncha) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.4f).fillMaxHeight()) {
                ContenidoListaSocial(
                    viewModel = viewModel,
                    codigoBusqueda = codigoBusqueda,
                    onCodigoChange = { codigoBusqueda = it },
                    textoFiltroAmigos = textoFiltroAmigos,
                    onFiltroChange = { textoFiltroAmigos = it },
                    mensajeUI = mensajeUI,
                    solicitudes = solicitudes,
                    amigosLista = amigosLista,
                    amigosFiltrados = amigosFiltrados,
                    // AÑADIDO: Los 3 parámetros también en la vista Tablet
                    onAmigoClick = { idChat, nombre, foto ->
                        idChatActivoId = idChat
                        nombreChatActivo = nombre
                        avatarChatActivo = foto
                    }
                )
            }

            VerticalDivider(color = Color.LightGray)

            Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                if (idChatActivoId != null) {
                    PantallaChatPrivado(
                        idChat = idChatActivoId!!,
                        nombreAmigo = nombreChatActivo, // AÑADIDO
                        avatarUrl = avatarChatActivo,   // AÑADIDO
                        onVolver = { idChatActivoId = null }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Selecciona un amigo para empezar a chatear", color = Color.Gray)
                    }
                }
            }
        }
    } else {
        if (idChatActivoId != null) {
            PantallaChatPrivado(
                idChat = idChatActivoId!!,
                nombreAmigo = nombreChatActivo,
                avatarUrl = avatarChatActivo,
                onVolver = { idChatActivoId = null }
            )
        } else {
            ContenidoListaSocial(
                viewModel = viewModel,
                codigoBusqueda = codigoBusqueda,
                onCodigoChange = { codigoBusqueda = it },
                textoFiltroAmigos = textoFiltroAmigos,
                onFiltroChange = { textoFiltroAmigos = it },
                mensajeUI = mensajeUI,
                solicitudes = solicitudes,
                amigosLista = amigosLista,
                amigosFiltrados = amigosFiltrados,
                onAmigoClick = { idChat, nombre, foto ->
                    idChatActivoId = idChat
                    nombreChatActivo = nombre
                    avatarChatActivo = foto
                }
            )
        }
    }
}