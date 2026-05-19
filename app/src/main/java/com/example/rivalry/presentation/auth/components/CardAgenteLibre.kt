package com.example.rivalry.presentation.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.presentation.auth.home.AgenteLibreUI

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