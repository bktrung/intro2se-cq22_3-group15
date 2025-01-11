package com.example.youmanage.screens.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.screens.task_management.ButtonSection
import com.example.youmanage.screens.task_management.TaskItem
import com.example.youmanage.utils.Constants.statusMapping
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.home.MyTaskViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyTaskScreen(
    paddingValues: PaddingValues = PaddingValues(),
    onClick: (String, String) -> Unit,
    myTaskViewModel: MyTaskViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(null)
    val myTask by myTaskViewModel.myTasks.observeAsState()

    var isSelectedButton by remember { mutableIntStateOf(0) }
    var filterTask by remember { mutableStateOf<List<Task>>(emptyList()) }

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            supervisorScope {
                val job = launch {
                    myTaskViewModel.getMyTasks(token)
                }
                job.join()
            }
        }
    }

    LaunchedEffect(
        key1 = isSelectedButton,
        key2 = myTask
    ) {
        if (myTask != null) {
            Log.d("TAG", "TaskListScreen: ${statusMapping[isSelectedButton]}")
            filterTask = myTask?.filter {
                it.status == statusMapping[isSelectedButton].second
            } ?: emptyList()
        }

        Log.d("TAG", "TaskListScreen: $filterTask")
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background),

        topBar = {
            TopBar(
                title = "My Task",
                color = Color.Transparent,
                haveLeading = false,
                trailing = {
                    Box(
                        modifier = Modifier.size(24.dp)
                    )
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

            ButtonSection(
                isSelectedButton = isSelectedButton,
                onClick = {
                    isSelectedButton = it
                },
                status = statusMapping
            )

            // Show "No Activity Log" when no logs are available
            if (filterTask.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No Task Found",
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
                    itemsIndexed(filterTask) { _, item ->
                        TaskItem(
                            title = item.title,
                            priority = item.priority,
                            assignee = item.assignee?.username ?: "Unknown",
                            endDate = item.endDate,
                            userId = item.assignee?.id ?: -1,
                            comments = item.commentsCount,
                            onTaskClick = {
                                onClick(item.id.toString(), item.project.toString())
                            }
                        )
                    }
                }
            }
        }
    }
}




