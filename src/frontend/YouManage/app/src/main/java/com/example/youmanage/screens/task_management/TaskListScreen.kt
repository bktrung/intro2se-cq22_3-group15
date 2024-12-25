package com.example.youmanage.screens.task_management

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel


@Composable
fun TaskListScreen(
    onNavigateBack: () -> Unit = {},
    projectId: String,
    onCreateTask: () -> Unit = {},
    onTaskDetail: (Int) -> Unit,
    onDisableAction: () -> Unit = {},
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {

    val backgroundColor = MaterialTheme.colorScheme.background

    val tasks by taskManagementViewModel.tasks.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    var filterTasks by remember { mutableStateOf(emptyList<Task>()) }
    val taskSocket by taskManagementViewModel.taskSocket.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val user by authenticationViewModel.user.observeAsState()

    val webSocketUrl = "${WEB_SOCKET}project/$projectId/"

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            taskManagementViewModel.getTasks(
                projectId = projectId,
                authorization = "Bearer $token"
            )
            authenticationViewModel.getUser("Bearer $token")
        }
        projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl)
        projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl)
    }

    LaunchedEffect(
        key1 = memberSocket,
        key2 = projectSocket
    ) {
        if (
            projectSocket is Resource.Success &&
            projectSocket?.data?.type == "project_deleted" &&
            projectSocket?.data?.content?.id.toString() == projectId
        ) {
            onDisableAction()
        }

        if (
            memberSocket is Resource.Success &&
            memberSocket?.data?.type == "member_removed" &&
            user is Resource.Success &&
            memberSocket?.data?.content?.affectedMembers?.contains(user?.data) == true
        ) {
            onDisableAction()
        }
    }

    var isSelectedButton by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        taskManagementViewModel.connectToTaskWebSocket(webSocketUrl)
    }

    LaunchedEffect(taskSocket) {
        taskManagementViewModel.getTasks(
            projectId = projectId,
            authorization = "Bearer ${accessToken.value}"
        )
    }

    LaunchedEffect(
        key1 = isSelectedButton,
        key2 = tasks
    ) {
        if (tasks is Resource.Success) {
            filterTasks = tasks?.data?.filter {
                it.status == statusMapping[isSelectedButton].second
            } ?: emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                onNavigateBack = { onNavigateBack() }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                //.background(Color(0xffF26A6A))
                .padding(paddingValues)
                .padding(top = 24.dp)
        ) {
            Column {
                ButtonSection(
                    isSelectedButton = isSelectedButton,
                    onClick = {
                        isSelectedButton = it
                    },
                    status = statusMapping
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(550.dp)

                    ) {
                        items(filterTasks.size) { index ->
                            TaskItem(
                                title = filterTasks[index].title,
                                priority = filterTasks[index].priority,
                                assignee = filterTasks[index].assignee.username,
                                endDate = filterTasks[index].endDate,
                                comments = filterTasks[index].commentsCount,
                                onCommentClick = {},
                                onTaskClick = { onTaskDetail(filterTasks[index].id) }
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onCreateTask()
                        },
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.border(
                            2.dp,
                            Color.Black,
                            RoundedCornerShape(30.dp)
                        )

                    ) {
                        Text(
                            "Create Task",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    title: String,
    priority: String?,
    assignee: String,
    endDate: String,
    comments: Int,
    onCommentClick: () -> Unit = {},
    onTaskClick: () -> Unit = {}
) {
    Card(
        onClick = { onTaskClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)

    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(50.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.Start)
                        .padding(end = 5.dp)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .then(
                            if (priority.isNullOrBlank()) Modifier
                                .size(10.dp)
                                .background(Color.Transparent) else Modifier
                        )
                ) {
                    priority?.let {
                        Text(
                            text = it,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Text(
                    assignee, fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.comment_icon),
                    contentDescription = "Comment",
                    tint = Color.Black,
                    modifier = Modifier.clickable { onCommentClick() }
                )

                Text(
                    comments.toString(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Icon(
                    painter = painterResource(id = R.drawable.calendar_icon),
                    contentDescription = "Deadline",
                    tint = Color.Black
                )
                Text(endDate, fontSize = 15.sp, fontWeight = FontWeight.Medium)

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
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp)
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onNavigateBack() }) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow_icon),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = "Task List",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Spacer(modifier = Modifier.size(30.dp))
    }
}

@Composable
fun ButtonSection(
    isSelectedButton: Int,
    onClick: (Int) -> Unit,
    status: List<Pair<String, String>>
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        status.forEachIndexed { index, name ->
            TaskListButton(
                name = name.first,
                contentColor = if (index == isSelectedButton) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                containerColor = if (index == isSelectedButton) MaterialTheme.colorScheme.primary else Color.Transparent,
                onClick = { onClick(index) }
            )
        }
    }
}


@Composable
fun TaskListButton(
    name: String,
    contentColor: Color,
    containerColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = Modifier
            .border(
                2.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(30.dp)
            )

    ) {
        Text(
            name,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 10.dp),
        )
    }
}

