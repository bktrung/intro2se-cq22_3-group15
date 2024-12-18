package com.example.youmanage.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.youmanage.screens.issue_management.AddIssueScreen
import com.example.youmanage.screens.issue_management.IssueDetailScreen
import com.example.youmanage.screens.issue_management.IssueListScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.issuesManagementNavGraph(
    rootNavController: NavHostController
) {
    navigation(
        route = Graph.ISSUE_MANAGEMENT, // Update if a separate graph is desired
        startDestination = IssuesManagementRouteScreen.IssueList.route
    ) {
        composable(IssuesManagementRouteScreen.IssueList.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            IssueListScreen(
                projectId = projectId ?: "",
                onNavigateBack = {
                    rootNavController.navigateUp()
                },
                onCreateIssue = {
                    rootNavController.navigate("create_issue/$projectId")
                },
                onIssueDetail = { issueId ->
                    rootNavController.navigate("issue_detail/$projectId/$issueId")
                }
            )
        }

        composable(IssuesManagementRouteScreen.CreateIssue.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            AddIssueScreen(
                projectId = projectId ?: "",
                onIssueCreated = {
                    rootNavController.navigateUp()
                },
                onNavigateBack = {
                    rootNavController.navigateUp()
                }
            )
        }
//
        composable(IssuesManagementRouteScreen.IssueDetail.route) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")
            val issueId = backStackEntry.arguments?.getString("issueId")
            IssueDetailScreen(
                projectId = projectId.toString(),
                issueId = issueId.toString(),
                onNavigateBack = {
                    rootNavController.navigateUp()
                }
            )
        }
    }
}
