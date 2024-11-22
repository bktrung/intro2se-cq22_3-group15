package com.example.youmanage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.youmanage.screens.authetication.CreateAccountScreen
import com.example.youmanage.screens.authetication.FindUserScreen
import com.example.youmanage.screens.authetication.LoginScreen
import com.example.youmanage.screens.authetication.OTPVerificationScreen
import com.example.youmanage.screens.authetication.ResetPasswordScreen
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
                },
                onForgotPassword = {
                    rootNavController.navigate(AuthRouteScreen.FindUser.route)
                }
            )
        }

        composable(AuthRouteScreen.OTPVerification.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email").orEmpty()
            val from = backStackEntry.arguments?.getString("from")

            OTPVerificationScreen(
                expiredTime = 300,
                from = from.toString(),
                onNavigateBack = {
                    val route = when (from) {
                        "1" -> AuthRouteScreen.CreateAccount.route
                        else -> AuthRouteScreen.FindUser.route
                    }
                    rootNavController.navigate(route)
                },
                onVerifySuccess = { string ->
                    val route = when (from) {
                        "1" -> AuthRouteScreen.Login.route
                        else -> "reset_password/$string"
                    }
                    rootNavController.navigate(route)
                },
                email = email
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
                onCreateSuccess = { email ->
                    rootNavController.navigate("otp_verification/$email/1")
                }
            )
        }

        composable(AuthRouteScreen.FindUser.route) {
            FindUserScreen(
                onNavigateBack = {
                    rootNavController.navigate(AuthRouteScreen.Login.route)
                },
                onFindSuccess = { email ->
                    rootNavController.navigate("otp_verification/$email/2")
                }
            )
        }

        composable(AuthRouteScreen.ResetPassword.route) {
            val resetToken = it.arguments?.getString("token")

            ResetPasswordScreen(
                onNavigateBack = {
                    rootNavController.navigate(AuthRouteScreen.Login.route)
                },
                resetToken = resetToken.toString(),
                onChangePasswordSuccess = {
                    rootNavController.navigate(AuthRouteScreen.Login.route)
                }
            )
        }
    }
}


