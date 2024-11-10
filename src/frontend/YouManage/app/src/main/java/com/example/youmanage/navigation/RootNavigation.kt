package com.example.youmanage.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.screens.LoadingScreen
import com.example.youmanage.screens.project_management.AddProjectScreen
import com.example.youmanage.screens.project_management.MainScreen
import com.example.youmanage.utils.isTokenExpired
import com.example.youmanage.viewmodel.AuthenticationViewModel
import kotlinx.coroutines.delay

object Graph {
    const val ROOT = "root_graph"
    const val LOADING = "loading_graph"
    const val AUTHENTICATION = "auth_graph"
    const val PROJECT_MANAGEMENT = "project_management_graph"
}

sealed class AuthRouteScreen(
    val route: String
) {
    data object Login : AuthRouteScreen("login")
    data object CreateAccount : AuthRouteScreen("create_account")
    data object Welcome : AuthRouteScreen("welcome")
    data object OTPVerification: AuthRouteScreen("otp_verification/{email}")
}

sealed class ProjectManagementRouteScreen(
    val route: String
) {
    data object Home : ProjectManagementRouteScreen("home")
    data object UserProfile : ProjectManagementRouteScreen("user_profile")
    data object Calender: ProjectManagementRouteScreen("calender")
    data object AddProject : ProjectManagementRouteScreen("add_project")
    data object ProjectDetail : ProjectManagementRouteScreen("project_detail")
    data object CreateTask : ProjectManagementRouteScreen("create_task")
    data object TaskList : ProjectManagementRouteScreen("task_list")
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun RootNavGraph(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    var isLoading by remember { mutableStateOf(true) }
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    var startDestination by remember { mutableStateOf(Graph.AUTHENTICATION) }

    LaunchedEffect(accessToken.value) {
        delay(500)
        isLoading = true

        startDestination = if (accessToken.value != null && !isTokenExpired(accessToken.value!!)) {
            Graph.PROJECT_MANAGEMENT
        } else {
            Graph.AUTHENTICATION
        }

        isLoading = false
    }

    if (isLoading) {
        startDestination = Graph.LOADING
    }

    NavHost(
        navController = navController,
        route = Graph.ROOT,
        startDestination = startDestination
    ) {

        authenticationNavGraph(
            rootNavController = navController
        )

        composable(route = Graph.PROJECT_MANAGEMENT) {
            MainScreen(rootNavController = navController,
                onAddNewProject = {
                    navController.navigate(ProjectManagementRouteScreen.AddProject.route)
                })

        }
        composable(route = Graph.LOADING) {
            LoadingScreen()
        }

        composable(route = ProjectManagementRouteScreen.AddProject.route) {
            AddProjectScreen(navHostController = navController)
        }
    }

}