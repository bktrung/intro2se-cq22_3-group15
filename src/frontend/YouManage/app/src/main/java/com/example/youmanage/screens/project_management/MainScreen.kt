package com.example.youmanage.screens.project_management

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.R
import com.example.youmanage.navigation.ProjectManagementNavGraph
import com.example.youmanage.navigation.ProjectManagementRouteScreen

data class BottomNavigationItem(
    val title: String,
    val icon: Int,
    val selectedColor: Color,
    val unselectedColor: Color,
    val route: String
)

val bottomNavigationItems = listOf(
    BottomNavigationItem(
        title = "Home",
        icon = R.drawable.home_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.Home.route

    ),
    BottomNavigationItem(
        title = "Task",
        icon = R.drawable.task_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ""
    ),

    BottomNavigationItem(
        title = "Bug",
        icon = R.drawable.bug_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.Issue.route
    ),

    BottomNavigationItem(
        title = "Calendar",
        icon = R.drawable.calendar_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.Calender.route
    ),
    BottomNavigationItem(
        title = "Profile",
        icon = R.drawable.user_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.UserProfile.route
    )
)

@Composable
fun MainScreen(
    rootNavController: NavHostController,
    onViewProject: (Int) -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        ProjectManagementNavGraph(
            paddingValues = paddingValues,
            rootNavController = rootNavController,
            homeNavController = navController

        )
    }
}


@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.onSurface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavigationItems.forEachIndexed { _, item ->

            IconButton(
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = item.icon),
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterVertically),
                    tint = if (currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true) Color.Black else Color.Gray
                )
            }
        }
    }
}


