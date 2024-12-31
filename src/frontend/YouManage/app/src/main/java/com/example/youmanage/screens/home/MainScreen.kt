package com.example.youmanage.screens.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.R
import com.example.youmanage.data.remote.notification.Notification
import com.example.youmanage.navigation.ProjectManagementNavGraph
import com.example.youmanage.navigation.ProjectManagementRouteScreen
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

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
        title = "My Task",
        icon = R.drawable.task_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.MyTask.route
    ),

    BottomNavigationItem(
        title = "Setting",
        icon = R.drawable.setting_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.Setting.route
    ),

    BottomNavigationItem(
        title = "Notification",
        icon = R.drawable.notification_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.Notification.route
    ),
    BottomNavigationItem(
        title = "Profile",
        icon = R.drawable.user_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.UserProfile.route
    )
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    rootNavController: NavHostController,
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val accessToken = authenticationViewModel.accessToken.collectAsState(null)
    val badgeCount by notificationViewModel.getUnreadCountFlow().collectAsState(null)

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let {
            supervisorScope {
                launch {
                    notificationViewModel.getUnreadCountNotifications("Bearer $it")
                    Log.d("Count in Main", badgeCount.toString())
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                badgeCount = badgeCount ?: 0
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )
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
    navController: NavHostController,
    badgeCount: Int = 0
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.1f)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavigationItems.forEachIndexed { _, item ->


            Box(
                modifier = Modifier.padding(12.dp)
                    .clickable {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
            ) {
                // Icon
                if (badgeCount > 0 && item.route == ProjectManagementRouteScreen.Notification.route) {
                    BadgedBox(
                        badge = {
                            Badge {
                                Text(
                                    badgeCount.toString(),
                                    modifier =
                                    Modifier.semantics {
                                        contentDescription = "$badgeCount new notifications"
                                    }
                                )
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier
                                .size(32.dp)
                                ,
                            tint = if (currentDestination?.hierarchy?.any {
                                    it.route == item.route
                                } == true) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(32.dp),
                        tint = if (currentDestination?.hierarchy?.any {
                                it.route == item.route
                            } == true) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    }
}