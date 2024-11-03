package com.example.youmanage.navigation


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.youmanage.screens.project_management.HomeScreen
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
            HomeScreen(paddingValues = paddingValues)
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
        composable(ProjectManagementRouteScreen.AddProject.route) {}
        composable(ProjectManagementRouteScreen.ProjectDetail.route) {}
        composable(ProjectManagementRouteScreen.CreateTask.route) {}
        composable(ProjectManagementRouteScreen.TaskList.route) {}
    }

}