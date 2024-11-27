package com.example.youmanage.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.youmanage.screens.task_management.CreateTaskScreen
import com.example.youmanage.screens.task_management.TaskDetailScreen
import com.example.youmanage.screens.task_management.TaskListScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.taskManagementNavGraph(
    rootNavController: NavHostController
) {

    navigation(
        route = Graph.TASK_MANAGEMENT,
        startDestination = "temp"
    ){
        composable(TaskManagementRouteScreen.TaskList.route) {
            val id = it.arguments?.getString("projectId")
            TaskListScreen(
                projectId = id ?: "",
                onNavigateBack = {
                    rootNavController.navigateUp()
                },
                onCreateTask = {
                    rootNavController.navigate("add_task/${id}")
                },
                onTaskDetail = {
                    taskId ->
                    Log.d("From Task List", "Task ID: $taskId Task ID: $id")
                    rootNavController.navigate("task_detail/$id/$taskId")
                }
            )
        }

        composable("temp"){
            Box(modifier = Modifier.fillMaxSize()) {

            }
        }

        composable(TaskManagementRouteScreen.CreateTask.route) {
            val id = it.arguments?.getString("projectId")
            CreateTaskScreen(
                navHostController = rootNavController,
                projectId = id ?: "",
                onCreateTask = {
                    rootNavController.navigateUp()
                }
            )
        }

        composable(TaskManagementRouteScreen.TaskDetail.route) {
            val projectId = it.arguments?.getString("projectId")
            val taskId = it.arguments?.getString("taskId")

            TaskDetailScreen(
                projectId = projectId.toString(),
                taskId = taskId.toString(),
                onNavigateBack = {
                    rootNavController.navigateUp()
                }
            )

        }
    }
}