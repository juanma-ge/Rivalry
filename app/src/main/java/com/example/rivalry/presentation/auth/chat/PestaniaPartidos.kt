package com.example.rivalry.presentation.auth.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Partido

@Composable
fun PestaniaPartidos(
    partidos: List<Partido>,
    esAdmin: Boolean,
    onAbrirFormulario: () -> Unit,
    onPartidoClick: (Partido) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        if (partidos.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No hay partidos programados aún.", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(partidos) { partido ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { onPartidoClick(partido)},
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Jornada ${partido.jornada}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(partido.nombreLocal, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)

                                if (partido.estado == "FINALIZADO") {
                                    Text("${partido.golesLocal} - ${partido.golesVisitante}", fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 16.dp))
                                } else {
                                    Text("vs", color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                                }

                                Text(partido.nombreVisitante, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (partido.estado == "FINALIZADO") "Finalizado" else "Pendiente",
                                fontSize = 12.sp,
                                color = if(partido.estado == "FINALIZADO") Color(0xFF4CAF50) else Color.Gray
                            )
                        }
                    }
                }
            }
        }

        if (esAdmin) {
            FloatingActionButton(
                onClick = onAbrirFormulario,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Partido")
            }
        }
    }
}