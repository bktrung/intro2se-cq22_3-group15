package com.example.youmanage.screens.project_management

import android.graphics.drawable.Icon
import android.transition.Visibility
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.R
import com.example.youmanage.navigation.Graph
import com.example.youmanage.navigation.ProjectManagementNavGraph
import com.example.youmanage.navigation.ProjectManagementRouteScreen
import com.example.youmanage.utils.randomColor

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
        route = ProjectManagementRouteScreen.TaskList.route
    ),
    BottomNavigationItem(
        title = "Calendar",
        icon = R.drawable.calendar_icon,
        selectedColor = Color.Black,
        unselectedColor = Color.Gray,
        route = ProjectManagementRouteScreen.Home.route
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
    rootNavController: NavHostController
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ },
                containerColor = Color.Black,
                contentColor = Color.White,
                modifier = Modifier
                    .size(70.dp)
                    .offset(y = (70).dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    contentDescription = "Add Project",
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
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
            .background(Color(0xFFF0F0F0)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        bottomNavigationItems.forEachIndexed { index, item ->

            if (index == 2) {
                Spacer(modifier = Modifier.width(50.dp))
            }

            IconButton(
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.findStartDestination().id)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.padding(16.dp)
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


