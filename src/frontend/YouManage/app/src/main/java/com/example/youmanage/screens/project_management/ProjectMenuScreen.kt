package com.example.youmanage.screens.project_management

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.R
import com.example.youmanage.navigation.ProjectManagementRouteScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel



data class ProjectMenuItem(
    val title: String,
    val icon: Int,
    val color: Color,
    val onClick: () -> Unit = {}
)


@Composable
fun ProjectMenuScreen(
    onNavigateBack: () -> Unit = {},
    onTaskList: () -> Unit = {},
    onIssueList: () -> Unit = {},
    onChatRoom: () -> Unit = {},
    onRoles: () -> Unit = {},
    onActivityLog: () -> Unit = {},
    onDeleteProjectSuccess: () -> Unit = {},
    onDisableAction: () -> Unit = {},
    onGanttChart: () -> Unit = {},
    id: String,
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val deleteProjectResponse by projectManagementViewModel.deleteProjectResponse.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val user by authenticationViewModel.user.observeAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDeleteErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(deleteProjectResponse) {
        if(deleteProjectResponse is Resource.Success){
            Log.d("Delete Project", "Success")
            onDeleteProjectSuccess()
        } else if (deleteProjectResponse is Resource.Error){
            Log.d("Delete Project", "Failed")
            showDeleteErrorDialog = true
        }
    }

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val webSocketUrl = "${WEB_SOCKET}project/${id}/"
            authenticationViewModel.getUser("Bearer $token")
            projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl)
            projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl)
        }
    }

    HandleOutProjectWebSocket(
        memberSocket = memberSocket,
        projectSocket = projectSocket,
        user = user,
        projectId = id,
        onDisableAction = onDisableAction
    )

    val projectMenuItems = listOf(
        ProjectMenuItem(
            title = "Task List",
            icon = R.drawable.task_icon,
            color = Color.Black,
            onClick = { onTaskList() }
        ),
        ProjectMenuItem(
            title = "Gantt Chart",
            icon = R.drawable.gantt_chart_icon,
            color = Color.Black,
            onClick = { onGanttChart() }
        ),
        ProjectMenuItem(
            title = "Issue List",
            icon = R.drawable.bug_icon,
            color = Color.Black,
            onClick = { onIssueList() }
        ),
        ProjectMenuItem(
            title = "Member",
            icon = R.drawable.user_icon,
            color = Color.Black
        ),
        ProjectMenuItem(
            title = "Activity Logs",
            icon = R.drawable.activity_logs,
            color = Color.Black,
            onClick = { onActivityLog() }
        ),
        ProjectMenuItem(
            title = "Project Setting",
            icon = R.drawable.setting_icon,
            color = Color.Black
        ),

        ProjectMenuItem(
            title = "Roles",
            icon = R.drawable.task_icon,
            color = Color.Black,
            onClick = { onRoles() }
        ),

        ProjectMenuItem(
            title = "Chat Room",
            icon = R.drawable.bubble_chat,
            color = Color.Black,
            onClick = {
                onChatRoom()
            }
        ),
        ProjectMenuItem(
            title = "Delete Project",
            icon = R.drawable.trash_icon,
            color = Color.Black,
            onClick = { showDeleteDialog = true }
        )
    )

    Scaffold(
        topBar = {
            TopBar(
                title = "Project Menu",
                trailing = {
                    Spacer(modifier = Modifier.size(24.dp))
                },
                color = Color.Transparent,
                onNavigateBack = { onNavigateBack() }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 42.dp)
            ) {

                items(projectMenuItems.size){
                    val item = projectMenuItems[it]
                    MenuItem(
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title,
                                tint = item.color,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        title = item.title,
                        onClick = item.onClick
                    )
                }
            }
        }
    }

    AlertDialog(
        title = "Delete this project?",
        content = "Are you sure you want to delete this project?",
        showDialog = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            accessToken.value?.let { token ->
                projectManagementViewModel.deleteProject(
                    id = id,
                    authorization = "Bearer $token"
                )
            }

            showDeleteDialog = false
        }
    )

    ErrorDialog(
        title = "Something went wrong?",
        content = "Can't delete this project? .Please try again later!",
        showDialog = showDeleteErrorDialog,
        onDismiss = { showDeleteErrorDialog = false },
        onConfirm = { showDeleteErrorDialog = false }
    )
}

@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    title: String,
    trailingIcon: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0x0D000000))
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)

        ) {
            trailingIcon()

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp

            )

            Spacer(modifier = modifier.size(24.dp))
        }
    }
}