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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.screens.components.GanttChart
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.generateChartData
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel

@Composable
fun GanttChartScreen(
    onNavigateBack: () -> Unit = {},
    projectId: String,
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {
    val backgroundColor = Color(0xffBAE5F5)

    val ganttChartData by projectManagementViewModel.ganttChartData.observeAsState()
    val project by projectManagementViewModel.project.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)

    // Lấy danh sách task
    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            projectManagementViewModel.getGanttChartData(
                id = projectId,
                authorization = "Bearer $token"
            )
        }
    }

    // Lấy thông tin Project
    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            projectManagementViewModel.getProject(
                id = projectId,
                authorization = "Bearer $token"
            )
        }
    }

    LaunchedEffect(key1 = ganttChartData) {
        if (ganttChartData is Resource.Success) {
            println("ganttChartData: ${ganttChartData?.data}")
        } else {
            println("ganttChartData: ${ganttChartData?.message}")
        }

    }


    LaunchedEffect(ganttChartData) {

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
            if (ganttChartData is Resource.Success && project is Resource.Success) {
                println("ganttChartData is Render: ${ganttChartData?.data}")
                println("project: ${project?.data}")
                GanttChart(
                    generateChartData(
                        tasks = ganttChartData!!.data!!,
                        projectDueDate = project!!.data!!.dueDate
                    )
                )
            } else {
                Text(
                    text = "Loading...",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 18.sp,
                    color = Color.Gray
                )
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
