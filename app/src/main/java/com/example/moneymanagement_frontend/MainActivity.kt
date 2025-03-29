package com.example.moneymanagement_frontend

import Composables.ProfileViewModel
import AuthActivityScreen
import Screens.MainScreen
import ViewModels.AuthViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.view.WindowCompat

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           MainScreen(viewModel = profileViewModel)
            val authViewModel: AuthViewModel by viewModels() // Inject ViewModel using Hilt
            AuthActivityScreen(authViewModel)
        }
    }
}