package com.example.youmanage.screens.project_management

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.projectmanagement.UserId
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.ChooseItemDialog
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import okhttp3.internal.filterList


data class ProjectMenuItem(
    val title: String,
    val icon: Int,
    val color: Color,
    val onClick: () -> Unit = {}
)


@Composable
fun ProjectMenuScreen(
    id: String,
    onNavigateBack: () -> Unit = {},
    onTaskList: () -> Unit = {},
    onIssueList: () -> Unit = {},
    onChatRoom: () -> Unit = {},
    onRoles: () -> Unit = {},
    onActivityLog: () -> Unit = {},
    onQuitProjectSuccess: () -> Unit = {},
    onDeleteProjectSuccess: () -> Unit = {},
    onDisableAction: () -> Unit = {},
    onGanttChart: () -> Unit = {},
    onChangeRequests: () -> Unit = {},
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val deleteProjectResponse by projectManagementViewModel.deleteProjectResponse.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val user by authenticationViewModel.user.observeAsState()
    val project by projectManagementViewModel.project.observeAsState()
    val empowerResponse by projectManagementViewModel.empowerResponse.observeAsState()
    val quitResponse by projectManagementViewModel.quitResponse.observeAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showQuitDialog by remember { mutableStateOf(false) }
    var showDeleteErrorDialog by remember { mutableStateOf(false) }
    var showChooseMemberDialog by remember { mutableStateOf(false) }

    var hostId by remember { mutableIntStateOf(-1) }
    var userId by remember { mutableIntStateOf(-2) }

    var onlyEmpower by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(deleteProjectResponse) {
        if(deleteProjectResponse is Resource.Success){
            onDeleteProjectSuccess()
        } else if (deleteProjectResponse is Resource.Error){
            showDeleteErrorDialog = true
        }
    }

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val webSocketUrl = "${WEB_SOCKET}project/${id}/"
            authenticationViewModel.getUser("Bearer $token")
            projectManagementViewModel.getProject(id = id, authorization = "Bearer $token")
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
            color = MaterialTheme.colorScheme.primary,
            onClick = { onTaskList() }
        ),
        ProjectMenuItem(
            title = "Gantt Chart",
            icon = R.drawable.gantt_chart_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = { onGanttChart() }
        ),
        ProjectMenuItem(
            title = "Issue List",
            icon = R.drawable.bug_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = { onIssueList() }
        ),
        ProjectMenuItem(
            title = "Activity Logs",
            icon = R.drawable.activity_logs,
            color = MaterialTheme.colorScheme.primary,
            onClick = { onActivityLog() }
        ),

        ProjectMenuItem(
            title = "Change Request",
            icon = R.drawable.activity_logs,
            color = MaterialTheme.colorScheme.primary,
            onClick = { onChangeRequests() }
        ),

        ProjectMenuItem(
            title = "Roles",
            icon = R.drawable.task_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = { onRoles() }
        ),

        ProjectMenuItem(
            title = "Chat Room",
            icon = R.drawable.bubble_chat,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                onChatRoom()
            }
        ),
        ProjectMenuItem(
            title = "Change Project Owner",
            icon = R.drawable.member_role_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                val size = project?.data?.members?.size ?: 1
                onlyEmpower = true
                if(size > 1){
                    if(userId == hostId){
                        showChooseMemberDialog = true
                    } else {
                        Toast.makeText(context, "You are not the project owner", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(context, "You are the last member of this project", Toast.LENGTH_SHORT).show()
                }
            }
        ),
        ProjectMenuItem(
            title = "Delete Project",
            icon = R.drawable.trash_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                if(userId == hostId){
                    showDeleteDialog = true
                } else{
                    Toast.makeText(context, "You are not the host of this project", Toast.LENGTH_SHORT).show()
                }
            }
        ),

        ProjectMenuItem(
            title = "Quit Project",
            icon = R.drawable.quit_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                val size = project?.data?.members?.size ?: 1
                if(size > 1){
                    if(userId == hostId){
                        showChooseMemberDialog = true
                    } else {
                        showQuitDialog = true
                    }
                } else{
                    Toast.makeText(context, "You are the last member of this project", Toast.LENGTH_SHORT).show()
                }
            }
        )
    )

    LaunchedEffect(empowerResponse){
        if(empowerResponse is Resource.Success && accessToken.value != null && !onlyEmpower){
            if(user?.data?.id != null){
                Log.d("Quit", accessToken.value.toString())
                projectManagementViewModel.quitProject(
                    id = id,
                    authorization = "Bearer ${accessToken.value}"
                )
            }
        }

        if(empowerResponse is Resource.Success && onlyEmpower){
            onlyEmpower = false
            Toast.makeText(context, "You are not now the project owner", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(quitResponse){
        if(quitResponse is Resource.Success && accessToken.value != null){
            onQuitProjectSuccess()
        }
    }

    LaunchedEffect(
        key1 = project,
        key2 = user
    ){
        if(project is Resource.Success && user is Resource.Success){
            hostId = project?.data?.host?.id ?: -1
            userId = user?.data?.id ?: -2
        }
    }

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
            .padding(WindowInsets.statusBars.asPaddingValues())


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
                items(projectMenuItems.size) { index ->
                    val item = projectMenuItems[index]
                    val shouldShowItem =
                        item.title != "Delete Project" || (item.title === "Delete Project" && userId == hostId)
                    if (shouldShowItem) {
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

    AlertDialog(
        title = "Quit this project?",
        content = "Are you sure you want to quit this project?",
        showDialog = showQuitDialog,
        onDismiss = { showQuitDialog = false },
        onConfirm = {
            accessToken.value?.let { token ->
                projectManagementViewModel.quitProject(
                    id = id,
                    authorization = "Bearer $token"
                )
            }
            showQuitDialog = false
        }
    )

    ErrorDialog(
        title = "Something went wrong?",
        content = "Can't delete this project? .Please try again later!",
        showDialog = showDeleteErrorDialog,
        onDismiss = { showDeleteErrorDialog = false },
        onConfirm = { showDeleteErrorDialog = false }
    )

    val memberList = if (project is Resource.Success) {
        val projectData = project?.data
        projectData?.members?.filter { it.id != projectData.host.id } ?: emptyList()
    } else {
        emptyList()
    }

    ChooseItemDialog(
        title = "Choose Member to assign host role: ",
        showDialog = showChooseMemberDialog,
        items = memberList,
        displayText = { it.username ?: "Unassigned" },
        onDismiss = { showChooseMemberDialog = false },
        onConfirm = {
           accessToken.value?.let { token ->
               projectManagementViewModel.empower(
                   id ,
                   userId = UserId(it.id),
                   authorization = "Bearer $token"
               )
           }
        }
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
            .background(MaterialTheme.colorScheme.surface)
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