package com.example.youmanage.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.screens.LoadingScreen
import com.example.youmanage.utils.isTokenExpired
import com.example.youmanage.viewmodel.AuthenticationViewModel
import kotlinx.coroutines.delay

object Graph {
    const val ROOT = "root_graph"
    const val LOADING = "loading_graph"
    const val AUTHENTICATION = "auth_graph"
    const val PROJECT_MANAGEMENT = "project_management_graph"
    const val TASK_MANAGEMENT = "task_management_graph"
    const val ISSUE_MANAGEMENT = "issue_management_graph"
}

sealed class AuthRouteScreen(
    val route: String
) {
    data object Login : AuthRouteScreen("login")
    data object CreateAccount : AuthRouteScreen("create_account")
    data object Welcome : AuthRouteScreen("welcome")
    data object OTPVerification: AuthRouteScreen("otp_verification/{email}/{from}")
    data object FindUser: AuthRouteScreen("find_user")
    data object ResetPassword: AuthRouteScreen("reset_password/{token}")
}

sealed class ProjectManagementRouteScreen(
    val route: String
) {
    data object Main: ProjectManagementRouteScreen("main")
    data object Home : ProjectManagementRouteScreen("home")
    data object UserProfile : ProjectManagementRouteScreen("user_profile")
    data object Calender: ProjectManagementRouteScreen("calender")
    data object Issue: ProjectManagementRouteScreen("issue")
    data object AddProject : ProjectManagementRouteScreen("add_project")
    data object ProjectDetail : ProjectManagementRouteScreen("project_detail/{id}")
    data object ProjectMenu: ProjectManagementRouteScreen("project_menu/{id}")
    data object ActivityLogs : ProjectManagementRouteScreen("activity_logs/{projectId}")
    data object Roles : ProjectManagementRouteScreen("roles/{projectId}")
    data object GanttChart : ProjectManagementRouteScreen("gantt_chart/{projectId}")


}

sealed class TaskManagementRouteScreen(
    val route: String
) {
    data object CreateTask: TaskManagementRouteScreen("add_task/{projectId}")
    data object TaskList : TaskManagementRouteScreen("task_list/{projectId}")
    data object TaskDetail : TaskManagementRouteScreen("task_detail/{projectId}/{taskId}")
}

sealed class ChatRouteScreen(
    val route: String
){
    data object ChatScreen: ChatRouteScreen("chat_room/{projectId}")
}

sealed class IssuesManagementRouteScreen(
    val route: String
) {
    data object IssueList : IssuesManagementRouteScreen("issue_list/{projectId}")
    data object CreateIssue : IssuesManagementRouteScreen("create_issue/{projectId}")
    data object IssueDetail : IssuesManagementRouteScreen("issue_detail/{projectId}/{issueId}")
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RootNavGraph(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    var isLoading by remember { mutableStateOf(true) }
    var startDestination by remember { mutableStateOf("") }

    LaunchedEffect(accessToken.value) {

        isLoading = true
        delay(500)

        startDestination = if (accessToken.value != null && !isTokenExpired(accessToken.value!!)) {
            Graph.PROJECT_MANAGEMENT
        } else {
            Graph.AUTHENTICATION
        }

        isLoading = false
    }

    if (isLoading) {
        LoadingScreen()
    } else {
        NavHost(
            navController = navController,
            route = Graph.ROOT,
            startDestination = startDestination
        ) {
            authenticationNavGraph(rootNavController = navController)
            projectManagementNavGraph(rootNavController = navController)
            taskManagementNavGraph(rootNavController = navController)
            issuesManagementNavGraph(rootNavController = navController)
        }
    }
}
