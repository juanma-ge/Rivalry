package com.example.rivalry.presentation.auth.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Liga
import com.example.rivalry.domain.model.MiembroUI

@Composable
fun PestaniaEquipos(liga: Liga?, miembrosLista: List<MiembroUI>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Participantes: ${liga?.idsMiembros?.size ?: 0} / ${liga?.maxParticipantes ?: 0}",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (miembrosLista.isEmpty()) {
            Text(
                "Cargando lista de jugadores...",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        } else {
            LazyColumn {
                items(miembrosLista) { miembro ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = miembro.nombre, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)

                            if (miembro.esAdmin) {
                                Text("👑 ADMIN", color = Color(0xFFDAA520), fontWeight = FontWeight.Black, fontSize = 11.sp)
                            } else {
                                Text("Jugador", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}