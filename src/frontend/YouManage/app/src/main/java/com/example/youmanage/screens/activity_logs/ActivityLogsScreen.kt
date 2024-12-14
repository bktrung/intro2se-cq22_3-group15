package com.example.youmanage.screens.activity_logs

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.data.remote.activitylogs.Activity
import com.example.youmanage.data.remote.activitylogs.ActivityLog
import com.example.youmanage.viewmodel.ActivityLogsViewModel
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityLogsScreen(
    projectId: String,
    token: String,
    onBackClick: () -> Unit // Thêm callback để xử lý sự kiện quay lại
) {
    val viewModel: ActivityLogsViewModel = hiltViewModel()
    val logs = viewModel.activityLogs.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadActivityLogs(projectId, token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity Logs") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(modifier = Modifier
                .padding(paddingValues) // Thêm padding để tránh che khuất nội dung
                .padding(16.dp)) {
                // Kiểm tra nếu logs có dữ liệu
                if (logs.value.isEmpty()) {
                    Text("No activity logs available.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(logs.value) { log ->
                            ActivityLogItem(log)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ActivityLogItem(log: Activity) {
    // Định dạng timestamp thành ngày giờ dễ đọc
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(log.timestamp))

    // Card chứa thông tin activity
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Action: ${log.action}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Details: ${log.details}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Time: $formattedDate", style = MaterialTheme.typography.bodySmall)
        }
    }
}