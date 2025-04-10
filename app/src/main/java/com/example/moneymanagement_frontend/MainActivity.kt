package com.example.moneymanagement_frontend

import DI.ViewModels.ProfileViewModel
import Screens.MainScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels

import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           MainScreen(viewModel = profileViewModel)
//            val authViewModel: AuthViewModel by viewModels() // Inject ViewModel using Hilt
//            AuthActivityScreen(authViewModel)
        }
    }
}