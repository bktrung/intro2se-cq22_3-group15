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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.formatToRelativeTime
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel
import kotlinx.coroutines.delay

val primaryColor = Color.Black.copy(alpha = 0.1f)

val statusMapping = listOf(
    "Pending" to "PENDING",
    "In Progress" to "IN_PROGRESS",
    "Done" to "COMPLETED"
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
    val members by projectManagementViewModel.members.observeAsState()
    val comments by taskManagementViewModel.comments.observeAsState()
    val comment by taskManagementViewModel.comment.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val response by taskManagementViewModel.response.observeAsState()
    val taskSocket by taskManagementViewModel.taskSocket.observeAsState()
    val commentDelete by taskManagementViewModel.deleteCommentResponse.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val user by authenticationViewModel.user.observeAsState()

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


    LaunchedEffect(taskSocket) {
        if (
            taskSocket is Resource.Success &&
            taskSocket?.data?.modelType == "task"
        ) {
            accessToken.value?.let { token ->
                taskManagementViewModel.getTask(projectId, taskId, "Bearer $token")
                taskManagementViewModel.getComments(projectId, taskId, "Bearer $token")
                projectManagementViewModel.getMembers(projectId, "Bearer $token")
            }
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

    var status by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var editTitle by rememberSaveable { mutableStateOf(title) }
    var username by rememberSaveable { mutableStateOf("") }
    var memberId by rememberSaveable { mutableIntStateOf(-1) }
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var isTime by rememberSaveable { mutableIntStateOf(0) }
    var description by rememberSaveable { mutableStateOf("Your Description") }
    var priority by rememberSaveable { mutableIntStateOf(-1) }

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

    LaunchedEffect(task) {
        if (task is Resource.Success) {
            title = task?.data?.title ?: "My Task"
            description = task?.data?.description ?: "Your Description"
            status = statusMapping.firstOrNull {
                it.second == (task?.data?.status ?: "PENDING")
            }?.first.toString()
            username = task?.data?.assignee?.username ?: ""
            startDate = task?.data?.startDate ?: ""
            endDate = task?.data?.endDate ?: ""
            memberId = task?.data?.assignee?.id ?: -1

            val priorityValue = task?.data?.priority?.lowercase()
                .toString()
                .replaceFirstChar { char ->
                    char.uppercase()
                }
            priority = priorityChoice.indexOf(priorityValue)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
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
                    showSaveDialog = true
                },
                onDeleteClick = {
                    showDeleteDialog = true
                }
            )
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollable),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    color = Color.Black,
                    modifier = Modifier.clickable {
                        showTitleEditor = !showTitleEditor
                    }
                )

                AnimatedVisibility(visible = showTitleEditor) {
                    TextField(
                        value = editTitle,
                        textStyle = TextStyle(fontSize = 20.sp, fontFamily = fontFamily),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.project_title_icon),
                                contentDescription = ""
                            )
                        },
                        onValueChange = { editTitle = it },
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
                    text = status,
                    onClick = { showStatusDialog = true },
                    backgroundColor = primaryColor
                )

                LabeledTextField(
                    label = "Description",
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Enter project description",
                    leadingIconRes = R.drawable.description_icon,
                    backgroundColor = primaryColor
                )

                PrioritySelector(
                    priorityChoice = priorityChoice,
                    priority = priority,
                    onPrioritySelected = {
                        priority = it
                    }
                )

                AssigneeSelector(
                    label = "Assignee",
                    avatarRes = R.drawable.avatar,
                    username = username,
                    onClick = {
                        showMemberDialog = true
                    }
                )

                DatePickerField(
                    label = "Start date",
                    date = startDate,
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
                    date = endDate,
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
                        showCommentEditor = true
                    }
                )
            }

            ChooseItemDialog(
                title = "Choose Status",
                showDialog = showStatusDialog,
                items = listOf("Pending", "In Progress", "Done"),
                displayText = { it },
                onDismiss = { showStatusDialog = false },
                onConfirm = {
                    status = it
                    showStatusDialog = false
                }
            )

            ChooseItemDialog(
                title = "Choose Member",
                showDialog = showMemberDialog,
                items = if (members is Resource.Success) members?.data!! else emptyList(),
                displayText = { it.username },
                onDismiss = { showMemberDialog = false },
                onConfirm = { user ->
                    memberId = user.id
                    username = user.username
                    showMemberDialog = false
                }
            )

            if (showDatePicker) {
                DatePickerModal(
                    onDateSelected = {
                        if (isTime == 1) {
                            startDate = it
                        } else if (isTime == 2) {
                            endDate = it
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
                    status = statusMapping.firstOrNull { it.first == status }?.second
                        ?: "PENDING",
                    assigneeId = memberId
                ),
                "Bearer ${accessToken.value}"
            )

            taskManagementViewModel.updateTask(
                projectId,
                taskId,
                TaskUpdate(
                    title = editTitle,
                    description = description,
                    startDate = startDate,
                    endDate = endDate,
                    priority = if (priority == -1) null else priorityChoice[priority].uppercase()
                ),
                authorization = "Bearer ${accessToken.value}"
            )

            Log.d("Task Update", if (priority == -1) "Failed" else priorityChoice[priority].uppercase())



            showTitleEditor = false
            showSaveDialog = false
        })

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
                    comments[index].author.username,
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
                .padding(vertical = 15.dp)
                .padding(bottom = 10.dp)
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


