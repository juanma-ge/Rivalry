package com.example.rivalry.presentation.auth.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    var equipoSeleccionado by remember { mutableStateOf<MiembroUI?>(null) }
    var nuevoNombreInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Equipos Inscritos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(16.dp))

        if (miembros.isEmpty()) {
            Text("Aún no hay equipos inscritos.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn {
                items(miembros) { miembro ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = miembro.nombreEquipo, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "Capitán: ${miembro.nombre}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    if (miembro.esAdmin) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "👑 Admin",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            if (miembro.id == miId || esAdmin) {
                                IconButton(onClick = {
                                    equipoSeleccionado = miembro
                                    nuevoNombreInput = miembro.nombreEquipo
                                    mostrarDialogo = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar nombre", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo && equipoSeleccionado != null) {
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
                        onCambiarNombre(equipoSeleccionado!!.id, nuevoNombreInput)
                        mostrarDialogo = false
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}