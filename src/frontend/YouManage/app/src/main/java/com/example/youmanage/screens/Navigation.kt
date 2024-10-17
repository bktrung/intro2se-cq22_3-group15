package com.example.youmanage.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "manage_task") {
        composable("manage_task") { ManageTaskScreen(navController) }
        composable("login") { LoginScreen2(navController) }
        composable("create_account") { CreateAccountScreen(navController) }
    }
}
