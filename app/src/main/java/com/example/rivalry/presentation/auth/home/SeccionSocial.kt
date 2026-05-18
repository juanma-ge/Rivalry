package com.example.rivalry.presentation.auth.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SeccionSocial(viewModel: SocialViewModel = viewModel()) {
    var codigoBusqueda by remember { mutableStateOf("") }

    val mensajeUI by viewModel.mensajeUI.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Añadir Amigo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = codigoBusqueda,
                onValueChange = { codigoBusqueda = it.uppercase() },
                label = { Text("Ej: RIV-A1B2C") },
                modifier = Modifier.weight(1f),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    viewModel.buscarYEnviarSolicitud(codigoBusqueda)
                    codigoBusqueda = ""
                },
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }

        mensajeUI?.let { mensaje ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensaje,
                color = if (mensaje.contains("Error") || mensaje.contains("No")) MaterialTheme.colorScheme.error else Color(0xFF4CAF50),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            LaunchedEffect(mensaje) {
                kotlinx.coroutines.delay(3000)
                viewModel.limpiarMensaje()
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Divider(color = Color.LightGray.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(16.dp))

        Text("Mensajes Directos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                Text(
                    text = "Aún no tienes mensajes.\n¡Busca a un amigo por su código para empezar a chatear!",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}