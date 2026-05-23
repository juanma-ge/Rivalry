package com.example.rivalry.presentation.auth.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TemaViewModel : ViewModel() {
    private val _esModoOscuro = MutableStateFlow(false)
    val esModoOscuro: StateFlow<Boolean> = _esModoOscuro

    fun alternarModo(activado: Boolean) {
        _esModoOscuro.value = activado
    }
}