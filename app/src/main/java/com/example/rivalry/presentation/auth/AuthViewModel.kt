package com.example.rivalry.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rivalry.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _contrasenia = MutableStateFlow("")
    val contrasenia: StateFlow<String> = _contrasenia

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    fun emailCambiado(nuevoEmail: String){
        _email.value = nuevoEmail
    }

    fun contraseniaCambiada(nuevaContrasenia: String){
        _contrasenia.value = nuevaContrasenia
    }

    fun login(){

        viewModelScope.launch {

            if(!email.value.contains("@")){
                _mensajeError.value = "Debe de introducir un correo electrónico con '@'."
                return@launch
            }

            _cargando.value = true
            _mensajeError.value = null

            val userId = authRepository.login(_email.value, _contrasenia.value)

            if(userId != null){
                _loginExitoso.value = true
            } else {
                _mensajeError.value = "Error al iniciar sesión. Comprueba de nuevo."
            }

            _cargando.value = false

        }

    }

    fun registrarse(){
        viewModelScope.launch {
            if(!email.value.contains("@")){
                _mensajeError.value = "Debe de introducir un correo electrónico con '@'."
                return@launch
            }

            if (contrasenia.value.length < 6) {
                _mensajeError.value = "La contraseña debe tener al menos 6 caracteres."
                return@launch
            }

            _cargando.value = true
            _mensajeError.value = null

            val userId = authRepository.registro(email.value, contrasenia.value)
            if (userId != null){
                _loginExitoso.value = true
            } else {
                _cargando.value = false
                _mensajeError.value = "Error: Este correo ya está registrado."
            }
        }
    }

    fun guardarPerfilEnBaseDeDatos(apodo: String, onExito: () -> Unit){

        viewModelScope.launch {

            val conseguirIDUsuario = FirebaseAuth.getInstance().currentUser?.uid
            val conseguirEmailUsuario = FirebaseAuth.getInstance().currentUser?.email

            if(conseguirIDUsuario != null && conseguirEmailUsuario != null ){
                val mapaUsuario = hashMapOf(
                    "id" to conseguirIDUsuario,
                    "email" to conseguirEmailUsuario,
                    "nombre" to apodo,
                    "avatarUrl" to "",
                    "esAdmin" to false
                )

                FirebaseFirestore.getInstance().collection("usuarios")
                    .document(conseguirIDUsuario)
                    .set(mapaUsuario)
                    .addOnSuccessListener {
                        _cargando.value = false
                        onExito()
                    }
                    .addOnFailureListener {
                        _cargando.value = false
                        _mensajeError.value = "Error al guardar los datos del perfil."
                    }

            } else {
                _cargando.value = true
                _mensajeError.value = "Error iniciando sesión."
            }
        }
    }

}