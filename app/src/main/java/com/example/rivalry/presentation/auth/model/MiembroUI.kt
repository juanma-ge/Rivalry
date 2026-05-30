package com.example.rivalry.presentation.auth.model

data class MiembroUI(
    val id: String,
    val nombre: String,
    val esAdmin: Boolean,
    val nombreEquipo: String = ""
)