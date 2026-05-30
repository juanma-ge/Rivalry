package com.example.rivalry.presentation.auth.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.presentation.auth.model.AgenteLibreUI

@Composable
fun PestaniaFichajes(
    agentesLibres: List<AgenteLibreUI>,
    esCapitan: Boolean,
    onFicharJugador: (AgenteLibreUI) -> Unit,
    onMensajeClick: (String) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var agenteSeleccionado by remember { mutableStateOf<AgenteLibreUI?>(null) }

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
                        mostrarBotonFichar = esCapitan,
                        onFicharClick = {
                            agenteSeleccionado = agente
                            mostrarDialogo = true
                        },
                        onMensajeClick = {
                            onMensajeClick(agente.id)
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogo && agenteSeleccionado != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
            },
            title = { Text("Fichar jugador") },
            text = {
                Text("¿Quieres incorporar a ${agenteSeleccionado!!.nombre} a tu equipo?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onFicharJugador(agenteSeleccionado!!)
                        mostrarDialogo = false
                    }
                ) { Text("Confirmar fichaje") }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun CardAgenteLibre(
    agente: AgenteLibreUI,
    mostrarBotonFichar: Boolean,
    onFicharClick: () -> Unit,
    onMensajeClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = agente.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = agente.email, fontSize = 13.sp, color = Color.Gray)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                IconButton(onClick = onMensajeClick) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Enviar Mensaje",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                if (mostrarBotonFichar) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Button(onClick = onFicharClick, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Fichar", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fichar")
                    }
                }
            }
        }
    }
}