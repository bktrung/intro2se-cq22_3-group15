package com.example.youmanage.screens.activity_logs

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.youmanage.data.remote.activitylogs.Activity
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.formatToRelativeTime
import com.example.youmanage.viewmodel.ActivityLogsViewModel
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityLogScreen(
    projectId: String = "",
    onNavigateBack: () -> Unit = {},
    onDisableAction: () -> Unit = {},
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    activityLogViewModel: ActivityLogsViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel()
) {
    val accessToken = authenticationViewModel.accessToken.collectAsState(null)
    val activityLogs by activityLogViewModel.activityLogs.collectAsState()
    val isLoading by activityLogViewModel.isLoading.collectAsState()

    val user by authenticationViewModel.user.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()

    val webSocketUrl = "${WEB_SOCKET}project/$projectId/"

    // Only trigger API call when accessToken is updated and activityLogs are empty
    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            if (activityLogs.isEmpty()) {
                // Tạo một SupervisorJob để quản lý các coroutine
                val supervisorJob = SupervisorJob()
                val scope = CoroutineScope(Dispatchers.IO + supervisorJob)

                // Khởi tạo các công việc cần thực hiện
                val getActivityLogsJob = scope.launch {
                    activityLogViewModel.getActivityLogs(
                        projectId = projectId,
                        authorization = "Bearer $token"
                    )
                }

                val connectToMemberWebsocketJob = scope.launch {
                    projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl)
                }

                val connectToProjectWebsocketJob = scope.launch {
                    projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl)
                }

                // Sử dụng joinAll để đợi tất cả các công việc hoàn thành
                try {
                    // Chờ tất cả các coroutine hoàn thành
                    joinAll(getActivityLogsJob, connectToMemberWebsocketJob, connectToProjectWebsocketJob)
                } catch (e: CancellationException) {
                    // Xử lý nếu một trong các job bị hủy
                    Log.e("LaunchedEffect", "A coroutine was cancelled", e)
                }
            }
        }
    }

    HandleOutProjectWebSocket(
        memberSocket = memberSocket,
        projectSocket = projectSocket,
        user = user,
        projectId = projectId,
        onDisableAction = onDisableAction
    )

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
                title = "Activity Log",
                color = Color.Transparent,
                trailing = {
                    Box(modifier = Modifier.size(10.dp))
                },
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Show "No Activity Log" when no logs are available
            if (activityLogs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Activity Log",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Display each activity
                    itemsIndexed(activityLogs) { index, activity ->
                        ActivityItems(activity = activity)

                        // Trigger loading when scrolled to the last item
                        if (index == activityLogs.size - 1 && !isLoading) {
                            activityLogViewModel.getMoreActivityLogs(
                                projectId = projectId,
                                authorization = "Bearer ${accessToken.value}"
                            )
                        }
                    }
                }

                // Show loading spinner when fetching more logs
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(bottom = 100.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ActivityItems(
    activity: Activity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .padding(end = 16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    activity.description,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    formatToRelativeTime(activity.timestamp),
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.pin),
                contentDescription = "pin",
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }

    }
}