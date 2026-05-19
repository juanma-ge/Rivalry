package com.example.rivalry.presentation.auth.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Liga
import com.example.rivalry.domain.model.MiembroUI

@Composable
fun PestaniaEquipos(
    liga: Liga?,
    miembros: List<MiembroUI>,
    miId: String?,
    esAdmin: Boolean = false,
    onCambiarNombre: (String, String) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }
    var equipoIdSeleccionado by remember { mutableStateOf<String?>(null) }
    var nuevoNombreInput by remember { mutableStateOf("") }

    val equiposAgrupados = miembros.groupBy { it.nombreEquipo }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Equipos Inscritos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (miembros.isEmpty()) {
            Text("Aún no hay equipos inscritos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn {
                items(equiposAgrupados.entries.toList()) { (nombreEquipo, grupo) ->
                    val capitan = grupo.find { liga?.nombresEquipos?.containsKey(it.id) == true } ?: grupo.first()
                    val jugadoresPlantilla = grupo.filter { it.id != capitan.id }

                    // Estado para expandir/contraer
                    var expandido by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { expandido = !expandido },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = nombreEquipo, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text("Capitán: ${capitan.nombre}", fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                                        if (capitan.esAdmin) {
                                            Box(
                                                modifier = Modifier
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("👑 Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            }
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (capitan.id == miId || esAdmin) {
                                        IconButton(onClick = {
                                            equipoIdSeleccionado = capitan.id
                                            nuevoNombreInput = nombreEquipo
                                            mostrarDialogo = true
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar nombre", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                    Icon(
                                        imageVector = if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Desplegar",
                                        tint = Color.Gray
                                    )
                                }
                            }

                            AnimatedVisibility(visible = expandido) {
                                Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Plantilla:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.DarkGray)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (jugadoresPlantilla.isEmpty()) {
                                        Text("Sin jugadores adicionales.", fontSize = 13.sp, color = Color.Gray)
                                    } else {
                                        jugadoresPlantilla.forEach { jugador ->
                                            Text("• ${jugador.nombre}", fontSize = 14.sp, modifier = Modifier.padding(vertical = 2.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo && equipoIdSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cambiar nombre del equipo") },
            text = {
                OutlinedTextField(
                    value = nuevoNombreInput,
                    onValueChange = { nuevoNombreInput = it },
                    label = { Text("Nuevo nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (nuevoNombreInput.isNotBlank()) {
                        onCambiarNombre(equipoIdSeleccionado!!, nuevoNombreInput)
                        mostrarDialogo = false
                    }
                }) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) { Text("Cancelar") }
            }
        )
    }
}