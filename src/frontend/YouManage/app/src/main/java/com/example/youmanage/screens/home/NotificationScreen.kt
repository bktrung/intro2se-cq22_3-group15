package com.example.youmanage.screens.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
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
import com.example.youmanage.data.remote.notification.Notification
import com.example.youmanage.data.remote.notification.Object
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.utils.formatToRelativeTime
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onItemClick: (Object) -> Unit,
    haveLeading: Boolean,
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(null)
    val notifications by notificationViewModel.notifications.observeAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let {
            supervisorScope {
               val job = launch {
                    notificationViewModel.getNotifications(
                        authorization = "Bearer $it"
                    )
                }

                job.join()
            }

        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background),

        topBar = {
            TopBar(
                title = "Notifications",
                color = Color.Transparent,
                haveLeading = haveLeading,
                trailing = {
                    IconButton(
                        onClick = {

                                    notificationViewModel.readAll("Bearer ${accessToken.value}")

                                  },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.read_all_icon),
                            contentDescription = "Read all",
                            tint = Color(0xff4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                onNavigateBack = {}
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
            if (notifications == null || notifications?.isEmpty() == true) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Notification",
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
                    itemsIndexed(notifications ?: emptyList()) { index, item ->
                        NotificationItem(
                            item,
                            onMarkAsRead = {
                                if(item.id != null){
                                    notificationViewModel.markAsRead(
                                        notificationId = item.id,
                                        authorization = "Bearer ${accessToken.value}"
                                    )
                                }
                            },
                            onDelete = {
                                if(item.id != null){
                                    notificationViewModel.deleteNotification(
                                        notificationId = item.id,
                                        authorization = "Bearer ${accessToken.value}"
                                    )
                                }
                            },

                            onClick = {
                                notificationViewModel.markAsRead(
                                    notificationId = item.id ?: -1,
                                    authorization = "Bearer ${accessToken.value}"
                                )
                                onItemClick(it)
                            }
                        )

                        val size = notifications?.size ?: 0
                        // Trigger loading when scrolled to the last item
                        if (index == size - 1 && !isLoading) {
                            notificationViewModel.getMoreActivityLogs(
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
fun NotificationItem(
    notification: Notification = Notification(),
    onClick: (com.example.youmanage.data.remote.notification.Object) -> Unit,
    onMarkAsRead: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClick(notification.objectContent ?: Object())
            },
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            2.dp,
            if (notification.isRead == true) MaterialTheme.colorScheme.primaryContainer else Color.Green
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Read all",
                    tint = Color(0xffFFC107),
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    notification.title ?: "",
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 10.dp)

                )


               IconWithDropdownMenu(
                   onMarkAsRead = onMarkAsRead,
                   onDelete = onDelete
               )

            }

            Text(
                notification.body ?: "",
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                if (notification.createdAt !== null) formatToRelativeTime(notification.createdAt) else "",
                textAlign = TextAlign.Start,
                color = Color.Gray,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}

@Composable
fun IconWithDropdownMenu(
    onMarkAsRead: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Trạng thái menu

    Box {
        // Icon
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More Options",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(24.dp)
                .clickable { expanded = true } // Mở menu khi nhấn vào icon
        )

        // DropdownMenu
        DropdownMenu(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            expanded = expanded,
            onDismissRequest = { expanded = false } // Đóng menu khi nhấn bên ngoài
        ) {
            // Mark as Read Option
            DropdownMenuItem(
                text = { Text("Mark as Read") },
                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    expanded = false // Đóng menu
                    onMarkAsRead() // Gọi hàm xử lý
                }
            )

            // Delete Option
            DropdownMenuItem(
                text = { Text("Delete") },
                colors = MenuDefaults.itemColors(textColor = MaterialTheme.colorScheme.primary),
                onClick = {
                    expanded = false // Đóng menu
                    onDelete() // Gọi hàm xử lý
                }
            )
        }
    }
}