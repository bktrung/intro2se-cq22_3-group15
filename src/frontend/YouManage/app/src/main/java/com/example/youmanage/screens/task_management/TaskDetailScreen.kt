package com.example.youmanage.screens.task_management

import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.AssigneeSelector
import com.example.youmanage.screens.components.ChooseItemDialog
import com.example.youmanage.screens.components.DatePickerField
import com.example.youmanage.screens.components.DatePickerModal
import com.example.youmanage.screens.components.DropdownStatusSelector
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.ui.theme.fontFamily
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Constants.priorityChoice
import com.example.youmanage.utils.Constants.statusMapping
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.formatToRelativeTime
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel
import kotlinx.coroutines.delay

val primaryColor = Color.Black.copy(alpha = 0.1f)

data class TaskState(
    val title: String = "",
    val editTitle: String = "",
    val description: String = "Your Description",
    val status: String = "",
    val username: String = "",
    val memberId: Int = -1,
    val startDate: String = "",
    val endDate: String = "",
    val priority: Int = -1
)


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskDetailScreen(
    projectId: String,
    taskId: String,
    onNavigateBack: () -> Unit = {},
    onDisableAction: () -> Unit = {},
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {

    val task by taskManagementViewModel.task.observeAsState()
    val taskUpdate by taskManagementViewModel.taskUpdate.observeAsState()
    val members by projectManagementViewModel.members.observeAsState()
    val comments by taskManagementViewModel.comments.observeAsState()
    val comment by taskManagementViewModel.comment.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val response by taskManagementViewModel.response.observeAsState()
    val commentDelete by taskManagementViewModel.deleteCommentResponse.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val user by authenticationViewModel.user.observeAsState()
    var update by remember { mutableStateOf(false) }

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            taskManagementViewModel.getTask(projectId, taskId, "Bearer $token")
            taskManagementViewModel.getComments(projectId, taskId, "Bearer $token")
            projectManagementViewModel.getMembers(projectId, "Bearer $token")
            authenticationViewModel.getUser("Bearer $token")
        }
    }

    val webSocketUrl = "${WEB_SOCKET}project/${projectId}/"

    LaunchedEffect(Unit) {
        projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl)
        projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl)
        taskManagementViewModel.connectToTaskWebSocket(webSocketUrl)
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

    val scrollable = rememberScrollState()
    var showStatusDialog by remember { mutableStateOf(false) }
    var showMemberDialog by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTitleEditor by rememberSaveable { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCommentEditor by remember { mutableStateOf(false) }

    var taskState by remember { mutableStateOf(TaskState()) }
    var newTask by remember { mutableStateOf(TaskUpdate()) }
    var isTime by rememberSaveable { mutableIntStateOf(0) }

    var currentComment by remember {
        mutableStateOf(
            Comment(
                id = 0,
                content = "",
                author = User("", 0, ""),
                createdAt = "", updatedAt = ""
            )
        )
    }

    LaunchedEffect(response) {
        if (response is Resource.Success) {
            showDeleteDialog = false
            onNavigateBack()
        }
    }

    LaunchedEffect(update) {
        if (update) {
            taskManagementViewModel.updateTask(
                projectId,
                taskId,
                newTask,
                "Bearer ${accessToken.value}"
            )
            update = false
        }
    }

    LaunchedEffect(
        key1 = comment,
        key2 = commentDelete
    ) {
        if (comment is Resource.Success || commentDelete is Resource.Success) {
            taskManagementViewModel.getComments(
                projectId,
                taskId,
                "Bearer ${accessToken.value}"
            )
        }
    }


    LaunchedEffect(taskUpdate) {
        if (taskUpdate is Resource.Success) {
            taskManagementViewModel.getTask(
                projectId,
                taskId,
                "Bearer ${accessToken.value}"
            )
        }
    }

    LaunchedEffect(task) {
        if (task is Resource.Success) {
            task?.data?.let {
                taskState = taskState.copy(
                    title = it.title,
                    editTitle = it.title,
                    description = it.description ?: "Your Description",
                    status = statusMapping.firstOrNull { item ->
                        item.second == it.status
                    }?.first.toString(),
                    username = it.assignee?.username ?: "Unassigned",
                    startDate = it.startDate,
                    endDate = it.endDate,
                    memberId = it.assignee?.id ?: -1,
                    priority = priorityChoice.indexOf(
                        it.priority?.lowercase()?.replaceFirstChar { char -> char.uppercase() }
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding(),
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                ),
            topBar = {
                TopBar(
                    title = "Task Detail",
                    trailing = {
                        Spacer(modifier = Modifier.size(24.dp))
                    },
                    color = Color.Transparent,
                    onNavigateBack = { onNavigateBack() }
                )
            },
            bottomBar = {
                TaskBottomBar(
                    onSaveClick = {
                        newTask = TaskUpdate(
                            title = taskState.editTitle,
                            description = taskState.description,
                            startDate = taskState.startDate,
                            endDate = taskState.endDate,
                            priority = if (taskState.priority == -1) null else priorityChoice[taskState.priority].uppercase()
                        )
                        showSaveDialog = true
                    },
                    onDeleteClick = {
                        showDeleteDialog = true
                    }
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp)
                    .verticalScroll(scrollable),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = taskState.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    color = Color.Black,
                    modifier = Modifier.clickable {
                        showTitleEditor = !showTitleEditor
                    }
                )

                AnimatedVisibility(visible = showTitleEditor) {
                    TextField(
                        value = taskState.editTitle,
                        textStyle = TextStyle(fontSize = 20.sp, fontFamily = fontFamily),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.project_title_icon),
                                contentDescription = ""
                            )
                        },
                        onValueChange = { taskState = taskState.copy(editTitle = it) },
                        placeholder = {
                            Text(
                                text = "Enter project title",
                                fontSize = 20.sp,
                                color = Color.Gray
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = primaryColor,
                            unfocusedContainerColor = primaryColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                DropdownStatusSelector(
                    text = taskState.status,
                    onClick = { showStatusDialog = true },
                    backgroundColor = primaryColor
                )

                LabeledTextField(
                    label = "Description",
                    value = taskState.description,
                    onValueChange = { taskState = taskState.copy(description = it) },
                    placeholder = "Enter project description",
                    leadingIconRes = R.drawable.description_icon,
                    backgroundColor = primaryColor
                )

                PrioritySelector(
                    priorityChoice = priorityChoice,
                    priority = taskState.priority,
                    onPrioritySelected = {
                        taskState = taskState.copy(priority = it)
                    }
                )

                AssigneeSelector(
                    label = "Assignee",
                    avatarRes = R.drawable.avatar,
                    username = taskState.username,
                    onClick = {
                        showMemberDialog = true
                    }
                )

                DatePickerField(
                    label = "Start date",
                    date = taskState.startDate,
                    onDateClick = {
                        isTime = 1
                        showDatePicker = true
                    },
                    iconResource = R.drawable.calendar_icon,
                    placeholder = "Enter start date",
                    containerColor = primaryColor
                )

                DatePickerField(
                    label = "End date",
                    date = taskState.endDate,
                    onDateClick = {
                        isTime = 2
                        showDatePicker = true
                    },
                    iconResource = R.drawable.calendar_icon,
                    placeholder = "Enter end date",
                    containerColor = primaryColor
                )

                CommentSection(
                    comments = if (comments is Resource.Success) comments?.data!! else emptyList(),
                    postComment = { content ->
                        taskManagementViewModel.postComment(
                            projectId,
                            taskId,
                            Content(content),
                            "Bearer ${accessToken.value}"
                        )
                    },
                    onClick = { comment ->
                        currentComment = comment

                        if (currentComment.author.id == user?.data?.id) {
                            showCommentEditor = true
                        }
                    }
                )
            }

            ChooseItemDialog(
                title = "Choose Status",
                showDialog = showStatusDialog,
                items = listOf("Pending", "In Progress", "Completed"),
                displayText = { it },
                onDismiss = { showStatusDialog = false },
                onConfirm = {

                    taskState = taskState.copy(status = it)
                    showStatusDialog = false
                }
            )

            val memberList = if (members is Resource.Success) members?.data!! else emptyList()

            ChooseItemDialog(
                title = "Choose Member",
                showDialog = showMemberDialog,
                items = memberList,
                displayText = { it.username ?: "Unknown" },
                onDismiss = { showMemberDialog = false },
                onConfirm = { user ->
                    taskState = taskState.copy(
                        username = user.username ?: "Unknown",
                        memberId = user.id
                    )

                    showMemberDialog = false
                }
            )

            if (showDatePicker) {
                DatePickerModal(
                    onDateSelected = {
                        if (isTime == 1) {
                            taskState = taskState.copy(startDate = it)
                        } else if (isTime == 2) {
                            taskState = taskState.copy(endDate = it)
                        }
                        isTime = 0
                    },
                    onDismiss = {
                        showDatePicker = false
                        isTime = 0
                    })
            }

            EditCommentDialog(
                title = "Edit comment",
                content = currentComment.content,
                showDialog = showCommentEditor,
                onDismiss = { showCommentEditor = false },
                onSave = { content ->
                    taskManagementViewModel.updateComment(
                        projectId,
                        taskId,
                        currentComment.id.toString(),
                        Content(content),
                        "Bearer ${accessToken.value}"
                    )
                    showCommentEditor = false
                },
                onDelete = {
                    taskManagementViewModel.deleteComment(
                        projectId,
                        taskId,
                        currentComment.id.toString(),
                        "Bearer ${accessToken.value}"
                    )

                    showCommentEditor = false
                }
            )


            AlertDialog(
                title = "Delete task?",
                content = "Are you sure you want to delete this task?",
                showDialog = showDeleteDialog,
                onDismiss = {
                    showDeleteDialog = false
                },
                onConfirm = {
                    taskManagementViewModel.deleteTask(
                        projectId,
                        taskId,
                        "Bearer ${accessToken.value}"
                    )
                    showDeleteDialog = false
                    onNavigateBack()
                }
            )

        }

    }

    AlertDialog(
        title = "Update task?",
        content = "Are you sure you want to update this task?",
        showDialog = showSaveDialog,
        onDismiss = {
            showSaveDialog = false
        },
        onConfirm = {

            taskManagementViewModel.updateTaskStatusAndAssignee(
                projectId,
                taskId,
                TaskUpdateStatus(
                    status = statusMapping.firstOrNull { it.first == taskState.status }?.second
                        ?: "PENDING",
                    assigneeId = taskState.memberId
                ),
                "Bearer ${accessToken.value}"
            )
            update = true
            showTitleEditor = false
            showSaveDialog = false
        })

}


@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    @DrawableRes leadingIconRes: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    placeholderColor: Color = Color.Gray
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = leadingIconRes),
                    contentDescription = null,
                    tint = textColor
                )
            },
            placeholder = {
                Text(
                    text = placeholder,
                    color = placeholderColor
                )
            },
            maxLines = Int.MAX_VALUE,
            shape = RoundedCornerShape(10.dp)
        )
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentSection(
    comments: List<Comment>,
    postComment: (String) -> Unit = {},
    onClick: (Comment) -> Unit

) {
    var content by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Comments",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp, max = 250.dp),
            verticalArrangement = Arrangement
                .spacedBy(10.dp)
        ) {
            items(comments.size) { index ->
                CommentItem(
                    comments[index].author.username ?: "Unknown",
                    comments[index].content,
                    comments[index].createdAt,
                    onClick = { onClick(comments[index]) }
                )
                Spacer(
                    modifier = Modifier
                        .height(1.dp)
                        .background(Color.Black)
                )
            }
        }

        TextField(
            value = content,
            onValueChange = { content = it },
            placeholder = {
                Text(
                    "Enter your comment"
                )
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.send_icon),
                    contentDescription = "Send",
                    modifier = Modifier.clickable {
                        postComment(content)
                        content = ""
                    },
                    tint = Color.Black
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentItem(
    username: String = "Tuong",
    comment: String = "Hello",
    createAt: String = "2024-10-09T15:08:57.555682Z",
    onClick: () -> Unit = {}
) {

    var relativeTime by remember {
        mutableStateOf(formatToRelativeTime(createAt))
    }
    LaunchedEffect(createAt) {
        while (true) {
            relativeTime = formatToRelativeTime(createAt)
            delay(60000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.avatar
                    ),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp, horizontal = 10.dp)
                    ) {
                        Text(
                            text = username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = comment,
                            fontSize = 18.sp
                        )

                        Text(
                            relativeTime,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun TaskBottomBar(
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        ) {
            Button(
                onClick = onSaveClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Save", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Button(
                onClick = onDeleteClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Delete", fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
fun EditCommentDialog(
    title: String,
    content: String = "Hello",
    showDialog: Boolean = true,
    onDismiss: () -> Unit = {},
    onDelete: () -> Unit = {},
    onSave: (String) -> Unit = {}
) {

    var comment by rememberSaveable {
        mutableStateOf(content)
    }

    LaunchedEffect(content) {
        comment = content
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontFamily = fontFamily,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        maxLines = Int.MAX_VALUE,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                        ) {
                            Text("Cancel", fontFamily = fontFamily)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onSave(comment) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save", color = Color.White, fontFamily = fontFamily)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Delete", color = Color.White, fontFamily = fontFamily)
                        }
                    }
                }
            }
        }
    }
}


