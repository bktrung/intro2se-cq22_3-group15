package com.example.youmanage.screens.project_management

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youmanage.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.ChooseItemDialog
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.projectmanagement.ProjectMenuViewModel
import com.example.youmanage.viewmodel.TraceInProjectViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.cancellation.CancellationException


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
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectMenuViewModel: ProjectMenuViewModel = hiltViewModel(),
    traceInProjectViewModel: TraceInProjectViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val shouldDisableAction by traceInProjectViewModel.observeCombinedLiveData(id)
        .observeAsState(false)

    val user by authenticationViewModel.user.observeAsState()

    val empowerResponse by projectMenuViewModel.empowerResponse.observeAsState()
    val quitResponse by projectMenuViewModel.quitProjectResponse.observeAsState()
    val deleteProjectResponse by projectMenuViewModel.deleteProjectResponse.observeAsState()

    val isHost by projectMenuViewModel.isHost.observeAsState()
    val memberList by projectMenuViewModel.memberList.observeAsState(emptyList())

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showQuitDialog by remember { mutableStateOf(false) }
    var showDeleteErrorDialog by remember { mutableStateOf(false) }
    var showChooseMemberDialog by remember { mutableStateOf(false) }
    var onlyEmpower by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(deleteProjectResponse) {
        if (deleteProjectResponse is Resource.Success) {
            onDeleteProjectSuccess()
        } else if (deleteProjectResponse is Resource.Error) {
            showDeleteErrorDialog = true
        }
    }

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val webSocketUrl = "${WEB_SOCKET}project/${id}/"

            // Khởi tạo supervisorScope để quản lý các coroutine
            supervisorScope {
                // Lưu các job vào danh sách
                val jobs = listOf(
                    launch {
                        traceInProjectViewModel.connectToWebSocketAndUser(
                            webSocketUrl,
                            token
                        )
                    },
                    launch {
                        projectMenuViewModel.getMemberList(
                            id,
                            "Bearer $token",
                            user?.data?.id ?: 0
                        )
                    },
                    launch { authenticationViewModel.getUser("Bearer $token") },
                    launch {
                        projectMenuViewModel.isHost(
                            id = id,
                            authorization = "Bearer $token"
                        )
                    },
                )

                // Đợi tất cả các coroutine hoàn thành
                try {
                    jobs.joinAll() // Đợi tất cả các job hoàn thành
                } catch (e: CancellationException) {
                    // Xử lý khi có một job bị hủy
                    Log.e("Project Menu Coroutines", "Job was cancelled", e)
                }
            }
        }
    }

    LaunchedEffect(shouldDisableAction) {
        if (shouldDisableAction) {
            onDisableAction()
        }
    }

    val projectMenuItems = listOf(
        ProjectMenuItem(
            title = "Tasks List",
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
            title = "Issues List",
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
            title = "Change Requests",
            icon = R.drawable.change_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = { onChangeRequests() }
        ),

        ProjectMenuItem(
            title = "Roles",
            icon = R.drawable.role_icon,
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

                val size = memberList.size

                onlyEmpower = true
                if (size > 1) {
                    if (isHost == true) {
                        showChooseMemberDialog = true
                    } else {
                        Toast.makeText(context, "You are not the project owner", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "You are the last member of this project",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ),
        ProjectMenuItem(
            title = "Delete Project",
            icon = R.drawable.trash_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = {
                if (isHost == true) {
                    showDeleteDialog = true
                } else {
                    Toast.makeText(
                        context,
                        "You are not the host of this project",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ),

        ProjectMenuItem(
            title = "Quit Project",
            icon = R.drawable.quit_icon,
            color = MaterialTheme.colorScheme.primary,
            onClick = {

                val size = memberList.size

                if (size > 1) {
                    if (isHost == true) {
                        showChooseMemberDialog = true
                    } else {
                        showQuitDialog = true
                    }
                } else {
                    Toast.makeText(
                        context,
                        "You are the last member of this project",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    )

    LaunchedEffect(empowerResponse) {
        if (empowerResponse is Resource.Success && accessToken.value != null && !onlyEmpower) {
            if (user?.data?.id != null) {
                Log.d("Quit", accessToken.value.toString())
                supervisorScope {
                    launch {
                        projectMenuViewModel.quitProject(
                            id = id,
                            authorization = "Bearer ${accessToken.value}"
                        )
                    }
                }
            }
        }

        if (
            empowerResponse is Resource.Success
            && onlyEmpower
        ) {
            onlyEmpower = false
            Toast.makeText(context, "You are not now the project owner", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(quitResponse) {
        if (quitResponse is Resource.Success && accessToken.value != null) {
            onQuitProjectSuccess()
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 42.dp)
            ) {
                items(projectMenuItems.size) { index ->
                    val item = projectMenuItems[index]
                    // Điều kiện để kiểm tra xem item có phải là "Delete Project" hoặc "Change Project Owner"
                    if (item.title === "Delete Project" || item.title === "Change Project Owner") {
                        if (isHost == true) {
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
                    } else {
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
                projectMenuViewModel.deleteProject(
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
                projectMenuViewModel.quitProject(
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

    ChooseItemDialog(
        title = "Choose Member to assign host role: ",
        showDialog = showChooseMemberDialog,
        items = memberList,
        displayText = { it.username ?: "Unassigned" },
        onDismiss = { showChooseMemberDialog = false },
        onConfirm = {
            accessToken.value?.let { token ->
                projectMenuViewModel.empower(
                    id,
                    userId = it.id,
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