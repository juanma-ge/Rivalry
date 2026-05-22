package com.example.rivalry

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rivalry.data.repository.AuthRepositoryImpl
import com.example.rivalry.domain.repository.AuthRepository
import com.example.rivalry.navigation.RivalryNavigation
import com.example.rivalry.presentation.auth.AuthViewModel
import com.example.rivalry.presentation.auth.PantallaLogin
import com.example.rivalry.ui.theme.RivalryTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebaseAuth = FirebaseAuth.getInstance()
        val authRepository = AuthRepositoryImpl(firebaseAuth)
        val viewModel = AuthViewModel(authRepository)
        setContent {
            RivalryTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RivalryNavigation(viewModel = viewModel)
                }
            }
        }
    }
}