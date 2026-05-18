package com.example.rivalry.presentation.auth.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Partido

@Composable
fun CardPartidoLiga(
    partido: Partido,
    onVerClasificacionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "LIGA — JORNADA ${partido.jornada}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${partido.nombreLocal} VS ${partido.nombreVisitante}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (partido.estado == "FINALIZADO" || partido.terminado) {
                Text(
                    text = "Resultado final: ${partido.golesLocal} - ${partido.golesVisitante}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            } else {
                Text(
                    text = "Estado: ${partido.estado.uppercase()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onVerClasificacionClick,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Ver liga en detalle")
            }
        }
    }
}