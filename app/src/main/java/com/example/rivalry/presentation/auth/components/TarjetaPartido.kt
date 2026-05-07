package com.example.rivalry.presentation.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.presentation.auth.home.PartidoSueltoViewModel

@Composable
fun TarjetaPartido(
    partido: com.example.rivalry.domain.model.PartidoSuelto,
    esMio: Boolean,
    viewModel: PartidoSueltoViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = partido.deporte.uppercase(),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${partido.idsJugadores.size}/${partido.maxJugadores} plazas",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("📅 ${partido.fecha} - 🕒 ${partido.hora}", fontSize = 14.sp)
            Text("📍 ${partido.ubicacion} (${partido.nivel})", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            if (esMio) {
                OutlinedButton(
                    onClick = { viewModel.salirDePartido(partido.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Salir del partido")
                }
            } else {
                Button(
                    onClick = { viewModel.unirseAPartido(partido.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Apuntarse")
                }
            }
        }
    }
}