package com.example.youmanage.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.example.youmanage.screens.activity_logs.ActivityLogScreen
import com.example.youmanage.screens.authetication.ChangePasswordScreen
import com.example.youmanage.screens.changerequest.ChangeRequestScreen

import com.example.youmanage.screens.chat.ChatScreenWithViewModel
import com.example.youmanage.screens.project_management.AddProjectScreen
import com.example.youmanage.screens.project_management.GanttChartScreen
import com.example.youmanage.screens.home.HomeScreen
import com.example.youmanage.screens.home.MainScreen
import com.example.youmanage.screens.home.MyTaskScreen
import com.example.youmanage.screens.home.NotificationScreen
import com.example.youmanage.screens.home.SettingsScreen
import com.example.youmanage.screens.project_management.MemberProfileScreen
import com.example.youmanage.screens.project_management.ProjectDetailScreen
import com.example.youmanage.screens.project_management.ProjectMenuScreen
import com.example.youmanage.screens.project_management.UpdateProjectScreen
import com.example.youmanage.screens.home.UserProfileScreen
import com.example.youmanage.screens.role.RolesScreen
import com.example.youmanage.utils.ThemePreferences

@RequiresApi(Build.VERSION_CODES.O)
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
        composable(ProjectManagementRouteScreen.Home.route
        ) {
            HomeScreen(
                paddingValues = paddingValues,
                onAddNewProject = {
                    rootNavController.navigate(ProjectManagementRouteScreen.AddProject.route)
                },
                onViewProject = {
                        id->
                    rootNavController.navigate("project_detail/${id}")
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
                },
                onChangePassword = {
                    rootNavController.navigate(ProjectManagementRouteScreen.ChangePassword.route)
                }
            )
        }

        composable(
            route = ProjectManagementRouteScreen.Notification.route,
            deepLinks = listOf(navDeepLink { uriPattern = "app://home/notification" })
        ) {
            NotificationScreen(
                paddingValues = paddingValues
            )
        }

        composable(ProjectManagementRouteScreen.Setting.route) {
            val themePreferences = ThemePreferences(context = rootNavController.context)
            SettingsScreen(
                themePreferences,
                paddingValues = paddingValues
            )
        }

        composable(ProjectManagementRouteScreen.MyTask.route){
            MyTaskScreen(
                paddingValues = paddingValues,
                onClick = {
                    taskId, projectId ->
                    rootNavController.navigate("task_detail/${projectId}/${taskId}")
                }
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.projectManagementNavGraph(
    rootNavController: NavHostController
) {
    navigation(
        route = Graph.PROJECT_MANAGEMENT,
        startDestination = ProjectManagementRouteScreen.Main.route
    ) {

        composable(ProjectManagementRouteScreen.Main.route) {
            MainScreen(
                rootNavController = rootNavController
            )
        }

        composable(
            route = ProjectManagementRouteScreen.Notification.route
        ){

                NotificationScreen(
                    paddingValues = WindowInsets.systemBars.asPaddingValues()
                )

        }

        composable(ProjectManagementRouteScreen.AddProject.route) {
            AddProjectScreen(
                onNavigateBack = {
                    rootNavController.navigateUp()
                }
            )
        }

        composable(
            route = ProjectManagementRouteScreen.ProjectDetail.route
        ) {
            val id = it.arguments?.getString("id")
            ProjectDetailScreen(
                onNavigateBack = {
                    rootNavController.navigateUp()
                },
                onClickMenu = {
                    rootNavController.navigate("project_menu/${id}")
                },
                onDisableAction = {
                    rootNavController.navigate(ProjectManagementRouteScreen.Main.route)
                },
                onUpdateProject = {
                    rootNavController.navigate("update_project/${id}")
                },
                onMemberProfile = {memberId ->
                    rootNavController.navigate("member_profile/${id}/${memberId}")
                },
                id = id!!.toInt()
            )
        }

        composable(
            route = ProjectManagementRouteScreen.UpdateProject.route
        ){
            val id = it.arguments?.getString("id")
            UpdateProjectScreen(
                onNavigateBack = {
                    rootNavController.navigateUp()
                },
                projectId = id!!.toString(),
                onDisableAction = {
                    rootNavController.navigate(ProjectManagementRouteScreen.Main.route)
                }
            )
        }

        composable(
            route = ProjectManagementRouteScreen.ProjectMenu.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300)
                ) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300)
                )
            }
        ) {
            val id = it.arguments?.getString("id")
            ProjectMenuScreen(
                onNavigateBack = {
                    rootNavController.navigateUp()
                },
                onIssueList = {
                    rootNavController.navigate("issue_list/${id}")
                },
                onTaskList = {
                    rootNavController.navigate("task_list/${id}")
                },
                onChatRoom = {
                    rootNavController.navigate("chat_room/${id}")
                },
                onActivityLog = {
                    rootNavController.navigate("activity_logs/${id}")
                },
                onDeleteProjectSuccess = {
                    rootNavController.navigate(ProjectManagementRouteScreen.Main.route)
                },
                onDisableAction = {
                    rootNavController.navigate(ProjectManagementRouteScreen.Main.route)
                },
                onRoles = {
                    rootNavController.navigate("roles/${id}")
                },
                onGanttChart = {
                    rootNavController.navigate("gantt_chart/${id}")
                },
                onQuitProjectSuccess = {
                    rootNavController.navigate(ProjectManagementRouteScreen.Main.route)
                },
                onChangeRequests = {
                    rootNavController.navigate("change_requests/${id}")
                },
                id = id.toString()
            )
        }

        composable(
            route = ChatRouteScreen.ChatScreen.route
        ){
            val projectId = it.arguments?.getString("projectId")

            ChatScreenWithViewModel(
                projectId = projectId ?: "",
                onNavigateBack = { rootNavController.navigateUp() })
        }

        composable(
            route = ProjectManagementRouteScreen.ActivityLogs.route
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
            ActivityLogScreen(
                projectId = projectId,
                onNavigateBack = { rootNavController.navigateUp() },
                onDisableAction = {
                    rootNavController.navigate(ProjectManagementRouteScreen.Main.route)
                }
            )
        }

        composable(
            route = ProjectManagementRouteScreen.Roles.route
        ){
            val projectId = it.arguments?.getString("projectId")
            RolesScreen(
                projectId = projectId ?: "",
                onNavigateBack = { rootNavController.navigateUp() }
            )
        }

        composable(ProjectManagementRouteScreen.GanttChart.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            if (projectId != null) {
                GanttChartScreen(
                    projectId = projectId,
                    onNavigateBack = {
                        rootNavController.navigateUp()
                    }
                )
            }
        }
        composable(route = ProjectManagementRouteScreen.ChangeRequest.route){
            val projectId = it.arguments?.getString("projectId")
            if (projectId != null) {
                ChangeRequestScreen(
                    projectId.toInt(),
                    onDisableAction = {},
                    onNavigateBack = {
                        rootNavController.navigateUp()
                    }
                )
            }
        }

        composable(
            route = ProjectManagementRouteScreen.MemberProfile.route
        ){
            val projectId = it.arguments?.getString("project_id")
            val memberId = it.arguments?.getString("member_id")

            MemberProfileScreen(
                projectId = projectId ?: "",
                memberId = memberId ?: "",
                onNavigateBack = {
                    rootNavController.navigateUp()
                }
            )
        }

        composable(ProjectManagementRouteScreen.ChangePassword.route){
            ChangePasswordScreen(
                onNavigateBack = {
                    rootNavController.navigate(AuthRouteScreen.Login.route)
                },
                onChangePasswordSuccess = {
                    rootNavController.navigate(AuthRouteScreen.Login.route) {
                        popUpTo(Graph.PROJECT_MANAGEMENT) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                }
            )
        }

    }
}