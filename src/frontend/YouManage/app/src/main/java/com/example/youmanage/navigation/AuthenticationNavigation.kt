package com.example.youmanage.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.screens.HomeScreen
import com.example.youmanage.screens.LoginScreen
import com.example.youmanage.screens.SignUpScreen
import com.example.youmanage.screens.WelComeScreen

@Composable
fun AuthenticationNavigation(
    modifier: Modifier = Modifier
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {

        composable("home") {
            HomeScreen()
        }

        composable("welcome") {
            WelComeScreen(
                onLoginClick = { navController.navigate("login") },
                onSignUpClick = { navController.navigate("signup") }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateBack = { navController.navigate("welcome") },
                navController = navController
            )
        }

        composable("signup") {
            SignUpScreen(
                navController = navController,
                onNavigateBack = { navController.navigate("welcome") }
            )
        }
    }
}