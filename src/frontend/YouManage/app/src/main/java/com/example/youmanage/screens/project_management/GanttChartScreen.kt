package com.example.youmanage.screens.project_management

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBars
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.GanttChartData
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.screens.components.GanttChart
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.generateChartData
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel

@Composable
fun GanttChartScreen(
    onNavigateBack: () -> Unit = {},
    onDisableAction: () -> Unit = {},
    projectId: String,
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {
    val backgroundColor = Color(0xffBAE5F5)

    val ganttChartData by projectManagementViewModel.ganttChartData.observeAsState()
    val project by projectManagementViewModel.project.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val user by authenticationViewModel.user.observeAsState()
    val taskSocket by taskManagementViewModel.taskSocket.observeAsState()

    var tasks by remember { mutableStateOf<List<GanttChartData>>(emptyList()) }
    var projectDueDate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(accessToken.value) {
        val url = "${WEB_SOCKET}project/$projectId/"
        accessToken.value?.let { token ->
            projectManagementViewModel.getGanttChartData(
                id = projectId,
                authorization = "Bearer $token"
            )

            projectManagementViewModel.getProject(
                id = projectId,
                authorization = "Bearer $token"
            )
            projectManagementViewModel.connectToProjectWebsocket(url)
            projectManagementViewModel.connectToMemberWebsocket(url)
            taskManagementViewModel.connectToTaskWebSocket(url)
            authenticationViewModel.getUser("Bearer $token")
        }
    }

    HandleOutProjectWebSocket(
        memberSocket = memberSocket,
        projectSocket = projectSocket,
        user = user,
        projectId = projectId,
        onDisableAction = onDisableAction
    )

    LaunchedEffect(taskSocket){
        if(taskSocket is Resource.Success) {
            projectManagementViewModel.getGanttChartData(
                id = projectId,
                authorization = "Bearer ${accessToken.value}"
            )
        }
    }

    LaunchedEffect(
        key1 = ganttChartData,
        key2 = project
    ) {
        if (ganttChartData is Resource.Success && project is Resource.Success) {
            isLoading = false
            tasks = ganttChartData?.data ?: emptyList()
            projectDueDate = project?.data?.dueDate ?: "No Data"
        } else {
            isLoading = true
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),
        topBar = {
            TopBar(
                title = "Gantt Chart",
                color = Color.Transparent,
                trailing = {
                    Box(
                        modifier = Modifier.size(24.dp)
                    )
                },
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Loading...")
                }

            } else {
                if(tasks.isNotEmpty()) {
                    GanttChart(
                        generateChartData(
                            tasks = tasks,
                            projectDueDate = projectDueDate
                        )
                    )
                }

            }

        }

    }
}



@Composable
fun TopBar(
    onNavigateBack: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(top = 24.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onNavigateBack() }) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow_icon),
                contentDescription = "Back"
            )
        }
        Text(
            text = "Gantt Chart",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.size(30.dp))
    }
}
