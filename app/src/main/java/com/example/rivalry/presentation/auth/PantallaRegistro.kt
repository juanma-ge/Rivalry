package com.example.rivalry.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaRegistro(viewModel: AuthViewModel, onRegistroExitoso: () -> Unit, onVolverALogin: () -> Unit) {

    val email by viewModel.email.collectAsState()
    val loginExitoso by viewModel.loginExitoso.collectAsState()
    val contrasenia by viewModel.contrasenia.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    LaunchedEffect(loginExitoso) {
        if (loginExitoso){
            onRegistroExitoso
        }
    }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text("Crear cuenta nueva", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.emailCambiado(it) },
            label = { Text("Correo electrónico")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contrasenia,
            onValueChange = {viewModel.contraseniaCambiada(it)},
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (mensajeError != null){
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        if (cargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = { viewModel.registrarse() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .width(40.dp)
            ) {
                Text("Iniciar Sesión", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {onVolverALogin},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .width(10.dp)
            ) {
                Text("Crear cuenta nueva", fontSize = 18.sp)
            }
        }

    }

}