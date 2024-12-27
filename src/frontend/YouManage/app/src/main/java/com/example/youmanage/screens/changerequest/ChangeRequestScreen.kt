package com.example.youmanage.screens.changerequest

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.notification.Notification
import com.example.youmanage.screens.home.IconWithDropdownMenu
import com.example.youmanage.screens.home.NotificationItem
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.formatToRelativeTime
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ChangeRequestViewModel
import com.example.youmanage.viewmodel.NotificationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChangeRequestScreen(
    projectId: Int,
    onDisableAction: () -> Unit,
    onNavigateBack: () -> Unit,
    changeRequestViewModel: ChangeRequestViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(null)
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val user by authenticationViewModel.user.observeAsState()

    val changeRequests by changeRequestViewModel.requests.observeAsState()

    val webSocketUrl = "${WEB_SOCKET}project/$projectId/"

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let {
            changeRequestViewModel.getChangeRequest(
                projectId = projectId,
                authorization = "Bearer $it"
            )
            projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl)
            projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl)
            authenticationViewModel.getUser("Bearer $it")
        }
    }



    HandleOutProjectWebSocket(
        projectSocket = projectSocket,
        memberSocket = memberSocket,
        user = user,
        projectId = projectId.toString(),
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

            // Show "No Activity Log" when no logs are available
            if (changeRequests is Resource.Success) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Display each activity
                    itemsIndexed(changeRequests?.data ?: emptyList()) { index, item ->
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Change Request",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}


//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun ChangeRequestItem(
//    request: ChangeRequest = ChangeRequest(),
//    onClick: () -> Unit = {}
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(10.dp)
//            .clip(RoundedCornerShape(10.dp)),
//        shape = RoundedCornerShape(10.dp),
//        border = BorderStroke(
//            2.dp,
//            Color.Green
//        ),
//        colors = CardDefaults.cardColors(
//            containerColor = MaterialTheme.colorScheme.primaryContainer
//        )
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 18.dp)
//                .padding(vertical = 20.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(10.dp),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                Text(
//                    request.systemDescription ?: "",
//                    textAlign = TextAlign.Start,
//                    fontWeight = FontWeight.SemiBold,
//                    modifier = Modifier.padding(start = 10.dp)
//
//                )
//            }
//
//            Text(
//                notification.body ?: "",
//                textAlign = TextAlign.Start,
//                color = MaterialTheme.colorScheme.primary,
//                modifier = Modifier.padding(start = 10.dp)
//            )
//
//            Spacer(modifier = Modifier.height(10.dp))
//
//            Text(
//                if (notification.createdAt !== null) formatToRelativeTime(notification.createdAt) else "",
//                textAlign = TextAlign.Start,
//                color = Color.Gray,
//                modifier = Modifier.padding(start = 10.dp)
//            )
//        }
//    }
//}
//
//
