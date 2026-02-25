package com.example.rivalry.presentation.auth

import android.R.attr.text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaLogin(viewModel: AuthViewModel, onLoginExitoso: () -> Unit) {

    val email by viewModel.email.collectAsState()
    val loginExitoso by viewModel.loginExitoso.collectAsState()
    val contrasenia by viewModel.contrasenia.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()


    LaunchedEffect(loginExitoso){
        if (loginExitoso){
            onLoginExitoso()
        }
    }

    Column(
        modifier = Modifier
            .padding(25.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){

        Text("RIVALRY", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Tu red social deportiva", fontSize = 15.sp, color = Color.Green)

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { text("Correo electrónico")},
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = contra,
            onValueChange = { contra = it },
            label = { text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        if (mensajeError != null){
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }

        if(cargando){
            CircularProgressIndicator()
        } else{
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Iniciar sesión", fontSize = 18.sp)
            }
        }

    }

}