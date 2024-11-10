package com.example.youmanage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.youmanage.screens.authetication.CreateAccountScreen
import com.example.youmanage.screens.authetication.LoginScreen
import com.example.youmanage.screens.authetication.OTPVerificationScreen
import com.example.youmanage.screens.authetication.WelcomeScreen
import com.example.youmanage.viewmodel.AuthenticationViewModel


fun NavGraphBuilder.authenticationNavGraph(
    rootNavController: NavHostController
) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthRouteScreen.Welcome.route
    ) {

        composable(AuthRouteScreen.Welcome.route) {
            WelcomeScreen(
                onLoginClick = {
                    rootNavController.navigate(AuthRouteScreen.Login.route)
                },
                onSignUpClick = {
                    rootNavController.navigate(AuthRouteScreen.CreateAccount.route)
                }
            )
        }

        composable(AuthRouteScreen.Login.route) {
            LoginScreen(
                onNavigateBack = {
                    rootNavController.navigate(AuthRouteScreen.Welcome.route)
                },
                onLoginSuccess = {
                    rootNavController.navigate(Graph.PROJECT_MANAGEMENT) {
                        popUpTo(AuthRouteScreen.Welcome.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(AuthRouteScreen.OTPVerification.route) { backStackEntry->
            val email = backStackEntry.arguments?.getString("email")
            OTPVerificationScreen(
                expiredTime = 300,
                onNavigateBack = {
                    rootNavController.navigate(AuthRouteScreen.CreateAccount.route)
                },
                onVerifySuccess = {
                    rootNavController.navigate(AuthRouteScreen.Login.route)
                },
                email = email.toString()
            )
        }

        composable(AuthRouteScreen.CreateAccount.route) {
            CreateAccountScreen(
                onNavigateBack = {
                    rootNavController.navigate(AuthRouteScreen.Welcome.route)
                },
                onLogin = {
                    rootNavController.navigate(AuthRouteScreen.Login.route)
                },
                onCreateSuccess = {
                    email->
                    rootNavController.navigate("otp_verification/$email")
                }
            )

        }
    }
}


