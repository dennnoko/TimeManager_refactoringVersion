package com.example.timemanager_refactoringversion.Screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationScreen() {
    val navController = rememberNavController()

    NavHost(navController, "HomeScreen") {
        composable("HomeScreen") { HomeScreen(navController) }
    }
}