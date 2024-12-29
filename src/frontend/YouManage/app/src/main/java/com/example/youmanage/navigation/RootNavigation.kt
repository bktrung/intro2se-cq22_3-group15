package com.example.youmanage.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.screens.LoadingScreen
import com.example.youmanage.screens.components.CustomSnackBar
import com.example.youmanage.ui.theme.YouManageTheme
import com.example.youmanage.utils.Constants.ACCESS_TOKEN_KEY
import com.example.youmanage.utils.Constants.REFRESH_TOKEN_KEY
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.ThemePreferences
import com.example.youmanage.utils.isTokenExpired
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.NotificationViewModel
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
    data object OTPVerification : AuthRouteScreen("otp_verification/{email}/{from}")
    data object FindUser : AuthRouteScreen("find_user")
    data object ResetPassword : AuthRouteScreen("reset_password/{token}")

}

sealed class ProjectManagementRouteScreen(
    val route: String
) {
    data object Main : ProjectManagementRouteScreen("main")
    data object Home : ProjectManagementRouteScreen("home")
    data object UserProfile : ProjectManagementRouteScreen("user_profile")
    data object Setting : ProjectManagementRouteScreen("setting")
    data object Notification : ProjectManagementRouteScreen("notification")

    data object MyTask : ProjectManagementRouteScreen("my_task")
    data object Issue : ProjectManagementRouteScreen("issue")
    data object AddProject : ProjectManagementRouteScreen("add_project")
    data object UpdateProject : ProjectManagementRouteScreen("update_project/{id}")
    data object ProjectDetail : ProjectManagementRouteScreen("project_detail/{id}")
    data object ProjectMenu : ProjectManagementRouteScreen("project_menu/{id}")
    data object MemberProfile :
        ProjectManagementRouteScreen("member_profile/{project_id}/{member_id}")


    data object ChangePassword: ProjectManagementRouteScreen("change_password")
    data object ActivityLogs : ProjectManagementRouteScreen("activity_logs/{projectId}")
    data object Roles : ProjectManagementRouteScreen("roles/{projectId}")
    data object GanttChart : ProjectManagementRouteScreen("gantt_chart/{projectId}")
    data object ChangeRequest : ProjectManagementRouteScreen("change_requests/{projectId}")
}

sealed class TaskManagementRouteScreen(
    val route: String
) {
    data object CreateTask : TaskManagementRouteScreen("add_task/{projectId}")
    data object TaskList : TaskManagementRouteScreen("task_list/{projectId}")
    data object TaskDetail : TaskManagementRouteScreen("task_detail/{projectId}/{taskId}")
}

sealed class ChatRouteScreen(
    val route: String
) {
    data object ChatScreen : ChatRouteScreen("chat_room/{projectId}")
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
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel()
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val notification by notificationViewModel.notificationFromSocket.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val user by authenticationViewModel.user.observeAsState()

    val navController = rememberNavController()

    val context = LocalContext.current
    val themePreferences = remember { ThemePreferences(context) }
    val isDarkMode by themePreferences.isDarkMode.collectAsState(initial = false)

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let {
            authenticationViewModel.getUser("Bearer $it")
        }
    }

    LaunchedEffect(user) {
        if (user is Resource.Success && user?.data?.id != null) {
            notificationViewModel.connectToWebSocket("${WEB_SOCKET}user/${user?.data?.id}/")
        }
    }
    var routeMessage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(notification) {
        if (notification is Resource.Success) {
            notification?.data?.let {
                snackBarHostState.showSnackbar(
                    it.body ?: "No notification",
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )

                val objectContent = it.objectContent

                if (objectContent?.projectId != null) {
                    routeMessage = if (objectContent.taskId != null) {
                        "task_detail/${objectContent.projectId}/${objectContent.taskId}"
                    } else if (objectContent.issueId != null) {
                        "issue_detail/${objectContent.projectId}/${objectContent.issueId}"
                    } else if (objectContent.changeRequestId != null) {
                        "change_requests/${objectContent.projectId}"
                    } else {
                        "project_detail/${objectContent.projectId}"
                    }
                }

                Log.d("RootNavGraph", routeMessage)
            }
        }
    }

    var seeDetail by remember { mutableStateOf(false) }

    LaunchedEffect(
        key1 = routeMessage,
        key2 = seeDetail
    ) {
        if (routeMessage.isNotEmpty() && seeDetail) {
            navController.navigate(routeMessage)
            seeDetail = false
        }
    }

    YouManageTheme(
        darkTheme = isDarkMode
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState) { data ->
                    CustomSnackBar(
                        message = data.visuals.message,
                        onSkipClick = {
                            snackBarHostState.currentSnackbarData?.dismiss()
                        },
                        onSeeClick = {
                            seeDetail = true
                            snackBarHostState.currentSnackbarData?.dismiss()
                        }
                    )
                }
            }
        ) { paddingValues ->

            val padding = paddingValues

            val refreshToken = authenticationViewModel.refreshToken.collectAsState(initial = null)
            val newAccessToken = authenticationViewModel.refreshResponse.observeAsState()
            var tokenExpired by rememberSaveable { mutableStateOf(false) }
            var isLoading by remember { mutableStateOf(true) }
            var startDestination by remember { mutableStateOf("") }

            fun stayedLogIn(): Boolean {
                return accessToken.value != null &&
                        !isTokenExpired(accessToken.value!!) &&
                        (newAccessToken.value !is Resource.Error)
            }

            LaunchedEffect(accessToken.value) {
                isLoading = true

                accessToken.value?.let { token ->
                    if (isTokenExpired(token)) {
                        tokenExpired = true
                        refreshToken.value?.let { refresh ->
                            authenticationViewModel.refreshAccessToken(
                                RefreshToken(refresh),
                                "Bearer $token"
                            )
                        }
                    }
                }

                startDestination = if (stayedLogIn()) {
                    Graph.PROJECT_MANAGEMENT
                } else {
                    Graph.AUTHENTICATION
                }

                delay(500)
                isLoading = false
            }

            LaunchedEffect(newAccessToken.value) {
                if (newAccessToken.value is Resource.Success && refreshToken.value != null) {
                    newAccessToken.value?.data?.access?.let { newToken ->
                        refreshToken.value?.let { refresh ->
                            authenticationViewModel.saveToken(
                                newToken,
                                refresh,
                                ACCESS_TOKEN_KEY,
                                REFRESH_TOKEN_KEY
                            )
                        }
                    }
                }

                if (!stayedLogIn()) {
                    startDestination = Graph.AUTHENTICATION
                }
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
    }
}