package com.example.youmanage.navigation


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.youmanage.screens.authetication.CreateAccountScreen
import com.example.youmanage.screens.authetication.FindUserScreen
import com.example.youmanage.screens.authetication.LoginScreen
import com.example.youmanage.screens.authetication.OTPVerificationScreen
import com.example.youmanage.screens.authetication.ResetPasswordScreen
import com.example.youmanage.screens.authetication.WelcomeScreen
import com.example.youmanage.screens.project_management.AddProjectScreen
import com.example.youmanage.screens.project_management.HomeScreen
import com.example.youmanage.screens.project_management.MainScreen
import com.example.youmanage.screens.project_management.ProjectDetailScreen
import com.example.youmanage.screens.project_management.UserProfileScreen

@Composable
fun ProjectManagementNavGraph(
    paddingValues: PaddingValues,
    rootNavController: NavHostController,
    homeNavController: NavHostController
) {
    NavHost(
        navController = homeNavController,
        route = Graph.PROJECT_MANAGEMENT,
        startDestination = ProjectManagementRouteScreen.Home.route
    )

    {
        composable(ProjectManagementRouteScreen.Home.route) {
            HomeScreen(
                paddingValues = paddingValues,
                onAddNewProject = {
                    rootNavController.navigate(ProjectManagementRouteScreen.AddProject.route)
                },
                onViewProject = {
                    rootNavController.navigate("project_detail/${it}")
                }
            )
        }

        composable(ProjectManagementRouteScreen.UserProfile.route) {
            UserProfileScreen(
                onLogout = {
                    rootNavController.navigate(Graph.AUTHENTICATION) {
                        popUpTo(Graph.PROJECT_MANAGEMENT) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(ProjectManagementRouteScreen.Calender.route) {}

    }

}

fun NavGraphBuilder.projectManagementNavGraph(
    rootNavController: NavHostController
) {
    navigation(
        route = Graph.PROJECT_MANAGEMENT,
        startDestination = ProjectManagementRouteScreen.Main.route
    ) {

        composable(ProjectManagementRouteScreen.Main.route) {
            MainScreen(
                rootNavController = rootNavController,
                onAddNewProject = {
                    rootNavController.navigate(ProjectManagementRouteScreen.AddProject.route)
                })
        }

        composable(ProjectManagementRouteScreen.ProjectDetail.route) {
                backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            ProjectDetailScreen(
                id = id!!.toInt()
            )
        }

        composable(ProjectManagementRouteScreen.AddProject.route) {
            AddProjectScreen(navHostController = rootNavController)
        }

        composable(route = ProjectManagementRouteScreen.ProjectDetail.route) {
            val id = it.arguments?.getString("id")
            ProjectDetailScreen(id = id!!.toInt())
        }

        composable(route = ProjectManagementRouteScreen.AddProject.route) {
            AddProjectScreen(navHostController = rootNavController)
        }

    }
}
