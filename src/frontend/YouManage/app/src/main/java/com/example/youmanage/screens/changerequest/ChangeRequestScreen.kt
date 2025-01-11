
package com.example.youmanage.screens.changerequest

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.changerequest.Reply
import com.example.youmanage.screens.components.ReplyChangeRequest
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.screens.task_management.ButtonSection
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.formatToRelativeTime
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.projectmanagement.ChangeRequestViewModel
import com.example.youmanage.viewmodel.TraceInProjectViewModel
import com.example.youmanage.viewmodel.projectmanagement.requestStatus
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChangeRequestScreen(
    projectId: Int,
    onDisableAction: () -> Unit,
    onNavigateBack: () -> Unit,
    changeRequestViewModel: ChangeRequestViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    traceInProjectViewModel: TraceInProjectViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(null)
    val isHost by changeRequestViewModel.isHost.observeAsState()
    val shouldDisableAction by traceInProjectViewModel.observeCombinedLiveData(projectId.toString())
        .observeAsState(false)

    val isLoading by changeRequestViewModel.isLoading.collectAsState()

    val changeRequests by changeRequestViewModel.requests.observeAsState()

    val webSocketUrl = "${WEB_SOCKET}project/$projectId/"

    var showReplyDialog by remember { mutableStateOf(false) }
    var selectedChangeRequest by remember { mutableStateOf(ChangeRequest()) }

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val authorization = "Bearer $token"

            // Sử dụng supervisorScope và join các coroutine
            supervisorScope {
                // Gửi các coroutine và đợi tất cả hoàn thành
                val changeRequestsJob = launch {
                    changeRequestViewModel.getChangeRequests(
                        projectId = projectId,
                        status = "PENDING",
                        authorization = authorization
                    )
                }
                val isHostJob = launch {
                    changeRequestViewModel.isHost(projectId.toString(), authorization)
                }
                val connectSocketJob = launch {
                    traceInProjectViewModel.connectToWebSocketAndUser(token, webSocketUrl)
                }

                // Đợi tất cả các coroutine hoàn thành
                joinAll(changeRequestsJob, isHostJob, connectSocketJob)
            }
        }
    }

    LaunchedEffect(shouldDisableAction) {
        if (shouldDisableAction) {
            onDisableAction()
        }
    }

    var isSelectedButton by remember { mutableIntStateOf(0) }

    LaunchedEffect(
        key1 = isSelectedButton
    ) {
        supervisorScope {
            launch {
                changeRequestViewModel.getChangeRequests(
                    projectId = projectId,
                    status = requestStatus[isSelectedButton].second,
                    authorization = "Bearer ${accessToken.value}"
                )
            }
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
                title = "Change Request",
                color = Color.Transparent,
                leading = {
                    Box(modifier = Modifier.size(10.dp))
                },
                trailing = {
                    Box(
                        modifier = Modifier.size(24.dp)
                    )
                },
                onNavigateBack = onNavigateBack
            )
        }
    ) { it ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            ButtonSection(
                isSelectedButton = isSelectedButton,
                onClick = {
                    isSelectedButton = it
                    changeRequestViewModel.getChangeRequests(
                        projectId = projectId,
                        status = requestStatus[it].second,
                        authorization = "Bearer ${accessToken.value}"
                    )
                },
                status = requestStatus
            )

            Column {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(550.dp)

                ) {
                    itemsIndexed(changeRequests ?: emptyList()) { index, item ->
                        ChangeRequestItem(
                            item,
                            onClick = {
                                if (isHost == true && item.status == "PENDING") {
                                    selectedChangeRequest = item
                                    showReplyDialog = true
                                }
                            }
                        )

                        if (index == changeRequests?.size?.minus(1) && !isLoading) {
                            changeRequestViewModel.loadMore(
                                projectId = projectId,
                                status = requestStatus[isSelectedButton].second,
                                authorization = "Bearer ${accessToken.value}"
                            )
                        }
                    }
                }

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

    ReplyChangeRequest(
        title = selectedChangeRequest.systemDescription ?: "",
        showDialog = showReplyDialog,
        onDismiss = {
            showReplyDialog = false
        },
        onApprove = {
            changeRequestViewModel.replyRequest(
                projectId = projectId,
                requestId = selectedChangeRequest.id ?: -1,
                reply = Reply(
                    action = "approve",
                    declinedReason = null
                ),
                authorization = "Bearer ${accessToken.value}"
            )
            showReplyDialog = false
        },
        onDecline = {
            changeRequestViewModel.replyRequest(
                projectId = projectId,
                requestId = selectedChangeRequest.id ?: -1,
                reply = Reply(
                    action = "reject",
                    declinedReason = it
                ),
                authorization = "Bearer ${accessToken.value}"
            )
            showReplyDialog = false
        }
    )
}


@Preview
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChangeRequestItem(
    request: ChangeRequest = ChangeRequest(),
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 2.dp,
            color = when (request.status) {
                "PENDING" -> MaterialTheme.colorScheme.secondary
                "REJECTED" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.primary
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row with Icon and Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.request),
                        contentDescription = "Request Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = request.systemDescription ?: "No Description",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Status and Additional Information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (request.status) {
                        "PENDING" -> "Status: Pending"
                        "REJECTED" -> "Status: Rejected"
                        "APPROVED" -> "Status: Approved"
                        else -> "Status: Unknown"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = when (request.status) {
                            "PENDING" -> MaterialTheme.colorScheme.secondary
                            "REJECTED" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                )
                Text(
                    text = formatToRelativeTime(request.createdAt ?: ""),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Declined Reason (if applicable)
            if (request.status == "REJECTED") {
                Text(
                    text = "Declined Reason: ${request.declinedReason ?: "No reason"}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}
