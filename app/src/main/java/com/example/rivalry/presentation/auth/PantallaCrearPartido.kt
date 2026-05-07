package com.example.rivalry.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Deporte
import com.example.rivalry.presentation.auth.home.PartidoSueltoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearPartido(
    viewModel: PartidoSueltoViewModel,
    onVolver: () -> Unit,
    onPartidoCreado: () -> Unit
) {
    var expandidoDeporte by remember { mutableStateOf(false) }
    var deporteSeleccionado by remember { mutableStateOf(Deporte.PADEL) } // Por defecto Pádel para pachangas

    var maxJugadores by remember { mutableFloatStateOf(4f) }
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }

    var expandidoNivel by remember { mutableStateOf(false) }
    var nivelSeleccionado by remember { mutableStateOf("Amateur") }
    val niveles = listOf("Amateur", "Intermedio", "Pro")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Organizar Pachanga", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onVolver() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expandidoDeporte,
                onExpandedChange = { expandidoDeporte = !expandidoDeporte }
            ) {
                OutlinedTextField(
                    value = deporteSeleccionado.nombreVisual,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Deporte") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoDeporte) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoDeporte,
                    onDismissRequest = { expandidoDeporte = false }
                ) {
                    Deporte.entries.forEach { deporte ->
                        DropdownMenuItem(
                            text = { Text(deporte.nombreVisual) },
                            onClick = { deporteSeleccionado = deporte; expandidoDeporte = false }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandidoNivel,
                onExpandedChange = { expandidoNivel = !expandidoNivel }
            ) {
                OutlinedTextField(
                    value = nivelSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Nivel requerido") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoNivel) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandidoNivel,
                    onDismissRequest = { expandidoNivel = false }
                ) {
                    niveles.forEach { nivel ->
                        DropdownMenuItem(
                            text = { Text(nivel) },
                            onClick = { nivelSeleccionado = nivel; expandidoNivel = false }
                        )
                    }
                }
            }

            Column {
                Text(text = "Plazas totales (incluyéndote): ${maxJugadores.toInt()}", fontWeight = FontWeight.Bold)
                Slider(
                    value = maxJugadores,
                    onValueChange = { maxJugadores = it },
                    valueRange = 2f..22f,
                    steps = 20
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Día (ej: 12 Oct)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Hora (ej: 19:00)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Club o pista (ej: Padelarium)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.crearPartido(
                        deporte = deporteSeleccionado.nombreVisual,
                        maxJugadores = maxJugadores.toInt(),
                        fecha = fecha,
                        hora = hora,
                        ubicacion = ubicacion,
                        nivel = nivelSeleccionado
                    ) {
                        onPartidoCreado()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = fecha.isNotBlank() && hora.isNotBlank() && ubicacion.isNotBlank()
            ) {
                Text("Publicar Partido", fontSize = 18.sp)
            }
        }
    }
}