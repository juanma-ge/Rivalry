package com.example.rivalry.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rivalry.presentation.auth.AuthViewModel
import com.example.rivalry.presentation.auth.PantallaHome
import com.example.rivalry.presentation.auth.PantallaLogin
import com.example.rivalry.presentation.auth.PantallaPerfil
import com.example.rivalry.presentation.auth.PantallaRegistro
import com.example.rivalry.presentation.auth.home.LigaViewModel
import com.example.rivalry.presentation.home.PantallaCrearLiga

@Composable
fun RivalryNavigation(viewModel: AuthViewModel) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login"){
        composable("login"){
            PantallaLogin(
                viewModel = viewModel,
                onLoginExitoso = {navController.navigate("home")},
                onNavegarARegistro = {navController.navigate("registro")}
            )
        }

        composable("registro"){
            PantallaRegistro(
                viewModel = viewModel,
                onRegistroExitoso = {navController.navigate("completar_perfil")},
                onVolverALogin = {navController.popBackStack()}
            )
        }

        composable("completar_perfil"){
            PantallaPerfil(
                viewModel = viewModel,
                onPerfilGuardado = { navController.navigate("home")} )
        }

        composable("home") {
            val ligaViewModel: LigaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            PantallaHome(
                viewModel = ligaViewModel,
                onNavegarACrearLiga = { navController.navigate("crear_liga") }
            )
        }

        composable("crear_liga") {
            PantallaCrearLiga(
                onVolver = { navController.popBackStack() }
            )
        }

    }

}