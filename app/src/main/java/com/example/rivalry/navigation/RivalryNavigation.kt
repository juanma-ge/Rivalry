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
import com.example.rivalry.presentation.auth.home.PartidoSueltoViewModel
import com.example.rivalry.presentation.home.PantallaCrearLiga
import com.example.rivalry.presentation.home.PantallaDetalleLiga
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RivalryNavigation(viewModel: AuthViewModel) {

    val navController = rememberNavController()

    val usuarioActual = FirebaseAuth.getInstance().currentUser
    val rutaInicial = if (usuarioActual != null) "home" else "login"

    NavHost(navController = navController, startDestination = rutaInicial){
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
            val partidoViewModel: PartidoSueltoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            PantallaHome(
                viewModel = ligaViewModel,
                partidoViewModel = partidoViewModel,
                onNavegarACrearLiga = { navController.navigate("crear_liga") },
                onNavegarADetalleLiga = { ligaId -> navController.navigate("detalle_liga/$ligaId") },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable("crear_liga") {
            val ligaViewModel: LigaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            PantallaCrearLiga(
                viewModel = ligaViewModel,
                onVolver = { navController.popBackStack() },
                onLigaCreada = {
                    navController.popBackStack()
                }
            )
        }

        composable("detalle_liga/{ligaId}") { backStackEntry ->
            val ligaId = backStackEntry.arguments?.getString("ligaId") ?: ""
            val ligaViewModel: LigaViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            PantallaDetalleLiga(
                ligaId = ligaId,
                viewModel = ligaViewModel,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}