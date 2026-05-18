package com.example.rivalry.presentation.auth.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.presentation.auth.components.CardAgenteLibre
import com.example.rivalry.presentation.auth.home.AgenteLibreUI

@Composable
fun PestaniaFichajes(
    agentesLibres: List<AgenteLibreUI>,
    esCapitan: Boolean,
    onFicharJugador: (String, String) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var agenteSeleccionado by remember { mutableStateOf<AgenteLibreUI?>(null) }
    var nombreEquipoAsignado by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Mercado de Fichajes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Jugadores esperando a ser asignados a un equipo.", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        if (agentesLibres.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay agentes libres en este momento.", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(agentesLibres) { agente ->
                    CardAgenteLibre(
                        agente = agente,
                        mostrarBoton = esCapitan,
                        onFicharClick = {
                            agenteSeleccionado = agente
                            mostrarDialogo = true
                        }
                    )
                }
            }
        }
    }

    // --- DIÁLOGO PARA CONFIRMAR FICHAJE ---
    if (mostrarDialogo && agenteSeleccionado != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
                nombreEquipoAsignado = ""
            },
            title = { Text("Fichar a ${agenteSeleccionado!!.nombre}") },
            text = {
                Column {
                    Text("Escribe el nombre de tu equipo para asignar a este jugador.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nombreEquipoAsignado,
                        onValueChange = { nombreEquipoAsignado = it },
                        label = { Text("Nombre de tu Equipo") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombreEquipoAsignado.isNotBlank()) {
                            onFicharJugador(agenteSeleccionado!!.id, nombreEquipoAsignado)
                            mostrarDialogo = false
                            nombreEquipoAsignado = ""
                        }
                    }
                ) {
                    Text("Confirmar Fichaje")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    nombreEquipoAsignado = ""
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}