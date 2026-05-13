package com.example.rivalry.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rivalry.domain.model.Deporte
import com.example.rivalry.presentation.auth.home.LigaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCrearLiga(viewModel: LigaViewModel, onVolver: () -> Unit, onLigaCreada: () -> Unit) {

    var nombre by remember { mutableStateOf("") }
    var maxParticipantes by remember { mutableFloatStateOf(20f) }
    var esPublica by remember { mutableStateOf(true) }

    var expandido by remember { mutableStateOf(false) }
    var deporteSeleccionado by remember { mutableStateOf(Deporte.FUTBOL_7) }

    var provincia by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nueva Liga", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del torneo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            ExposedDropdownMenuBox(
                expanded = expandido,
                onExpandedChange = { expandido = !expandido }
            ) {
                OutlinedTextField(
                    value = deporteSeleccionado.nombreVisual,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Deporte") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandido,
                    onDismissRequest = { expandido = false }
                ) {
                    Deporte.entries.forEach { deporte ->
                        DropdownMenuItem(
                            text = { Text(deporte.nombreVisual) },
                            onClick = {
                                deporteSeleccionado = deporte
                                expandido = false
                            }
                        )
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = provincia,
                    onValueChange = { provincia = it },
                    label = { Text("Provincia (ej: Madrid)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = { Text("Ciudad (ej: Getafe)") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            Column {
                Text(
                    text = "Límite de equipos: ${maxParticipantes.toInt()}",
                    fontWeight = FontWeight.Bold
                )
                Slider(
                    value = maxParticipantes,
                    onValueChange = { maxParticipantes = it },
                    valueRange = 4f..20f,
                    steps = 15
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Liga Pública", fontWeight = FontWeight.Bold)
                    Text(
                        text = if (esPublica) "Aparecerá en la pestaña Explorar." else "Solo se podrá entrar con enlace o código privado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = esPublica,
                    onCheckedChange = { esPublica = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.crearLigaEnFirebase(
                    nombre = nombre,
                    deporte = deporteSeleccionado,
                    maxParticipantes = maxParticipantes.toInt(),
                    esPublica = esPublica,
                    provincia = provincia,
                    ciudad = ciudad,
                    onExito = { onLigaCreada() }
                ) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = nombre.isNotBlank() && provincia.isNotBlank() && ciudad.isNotBlank()
            ) {
                Text("Crear Competición", fontSize = 18.sp)
            }
        }
    }
}