package com.example.youmanage.screens.task_management

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.AssigneeSelector
import com.example.youmanage.screens.components.ChangeRequestDialog
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
import com.example.youmanage.utils.randomAvatar
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.TraceInProjectViewModel
import com.example.youmanage.viewmodel.taskmanagement.TaskDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.coroutines.cancellation.CancellationException


val primaryColor: Color
    @Composable
    get() = MaterialTheme.colorScheme.surface

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
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    traceInProjectViewModel: TraceInProjectViewModel= hiltViewModel(),
    taskDetailViewModel: TaskDetailViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)

    val user by authenticationViewModel.user.observeAsState()
    var update by remember { mutableStateOf(false) }

    val shouldDisableAction by traceInProjectViewModel.observeCombinedLiveData(projectId)
        .observeAsState(false)

    val response by taskDetailViewModel.response.observeAsState()
    val commentDelete by taskDetailViewModel.deleteCommentResponse.observeAsState()
    val isHost by taskDetailViewModel.isHost.observeAsState()
    val task by taskDetailViewModel.task.observeAsState()
    val taskUpdate by taskDetailViewModel.taskUpdate.observeAsState()
    val members by taskDetailViewModel.members.observeAsState()
    val comments by taskDetailViewModel.comments.observeAsState()
    val comment by taskDetailViewModel.comment.observeAsState()
    val changeRequestResponse by taskDetailViewModel.requestResponse.observeAsState()

    var requestDescription by remember { mutableStateOf("") }

    val scrollable = rememberScrollState()
    var showStatusDialog by remember { mutableStateOf(false) }
    var showMemberDialog by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTitleEditor by rememberSaveable { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showCommentEditor by remember { mutableStateOf(false) }
    var showRequestDialog by remember { mutableStateOf(false) }

    var taskState by remember { mutableStateOf(TaskState()) }
    var newTask by remember { mutableStateOf(TaskUpdate()) }
    var isTime by rememberSaveable { mutableIntStateOf(0) }
    var currentComment by remember { mutableStateOf(Comment()) }

    var requestMessage by remember { mutableStateOf("") }
    var requestBody by remember { mutableStateOf(SendChangeRequest()) }

    val context = LocalContext.current

    val webSocketUrl = "${WEB_SOCKET}project/${projectId}/"

    LaunchedEffect(accessToken.value) {
        try {
            accessToken.value?.let { token ->
                supervisorScope {
                    // Launching coroutines concurrently
                    val job1 = launch {
                        taskDetailViewModel.loadTaskDetails(projectId, taskId, "Bearer $token")
                    }
                    val job2 = launch {
                         traceInProjectViewModel.connectToWebSocketAndUser(token, webSocketUrl)
                    }
                    val job3 = launch {
                        authenticationViewModel.getUser("Bearer $token")
                    }

                    // Optionally, you can wait for all jobs to complete if necessary:
                    job1.join()
                    job2.join()
                    job3.join()
                }
            }
        } catch (e: CancellationException) {
            Log.d("Coroutine", "Job was cancelled: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.d("Coroutine", "Exception: ${e.localizedMessage}")
        }
    }

    LaunchedEffect(shouldDisableAction) {
        if (shouldDisableAction) {
            onDisableAction()
        }
    }

    LaunchedEffect(response, changeRequestResponse) {
        try {

            if(changeRequestResponse is Resource.Success){
                Toast.makeText(context, "Request sent successfully!", Toast.LENGTH_SHORT).show()
            } else if(changeRequestResponse is Resource.Error) {
                Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            }

            if (response is Resource.Success) {
                showDeleteDialog = false
                onNavigateBack()
            } else if (response is Resource.Error){
                Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: CancellationException) {
            Log.d("Coroutine", "Job was cancelled: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.d("Coroutine", "Exception: ${e.localizedMessage}")
        }
    }

    LaunchedEffect(update) {
        try {
            if (update) {
                supervisorScope {
                    launch {
                        taskDetailViewModel.updateTask(
                            projectId,
                            taskId,
                            newTask,
                            "Bearer ${accessToken.value}"
                        )
                    }
                }

                update = false
            }
        } catch (e: CancellationException) {
            Log.d("Coroutine", "Job was cancelled: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.d("Coroutine", "Exception: ${e.localizedMessage}")
        }

    }

    LaunchedEffect(
        key1 = comment,
        key2 = commentDelete
    ) {
        try{
            if (comment is Resource.Success || commentDelete is Resource.Success) {
                supervisorScope {
                    launch {
                        taskDetailViewModel.getComments(
                            projectId,
                            taskId,
                            "Bearer ${accessToken.value}"
                        )
                    }
                }

            }
        } catch (e: CancellationException) {
            Log.d("Coroutine", "Job was cancelled: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.d("Coroutine", "Exception: ${e.localizedMessage}")
        }

    }

    LaunchedEffect(taskUpdate) {
        try {
            if (taskUpdate is Resource.Success) {
                supervisorScope {
                    launch {
                        taskDetailViewModel.getTask(
                            projectId,
                            taskId,
                            "Bearer ${accessToken.value}"
                        )
                    }
                }
            }
        } catch (e: CancellationException) {
            Log.d("Coroutine", "Job was cancelled: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.d("Coroutine", "Exception: ${e.localizedMessage}")
        }

    }

    LaunchedEffect(task) {
        try{
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
        } catch (e: CancellationException) {
            Log.d("Coroutine", "Job was cancelled: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.d("Coroutine", "Exception: ${e.localizedMessage}")
        }


    }


    LaunchedEffect(changeRequestResponse){
        try{
            if(changeRequestResponse is Resource.Success){
                Toast.makeText(context, "Request sent successfully!", Toast.LENGTH_SHORT).show()
            } else if (changeRequestResponse is Resource.Error){
                Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: CancellationException) {
            Log.d("Coroutine", "Job was cancelled: ${e.localizedMessage}")
        } catch (e: Exception) {
            Log.d("Coroutine", "Exception: ${e.localizedMessage}")
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
                title = "Task Detail",
                trailing = {
                    Spacer(modifier = Modifier.size(24.dp))
                },
                color = Color.Transparent,
                onNavigateBack = { onNavigateBack() }
            )
        },
        bottomBar = {
            ButtonBottomBar(
                onSaveClick = {
                    if(isHost == true){
                        newTask = TaskUpdate(
                            title = taskState.editTitle,
                            description = taskState.description,
                            startDate = taskState.startDate,
                            endDate = taskState.endDate,
                            priority = if (taskState.priority == -1) null else priorityChoice[taskState.priority].uppercase()
                        )

                        showSaveDialog = true
                    } else {
                        requestMessage = "Send request to update this task?"
                        requestBody = SendChangeRequest(
                            requestType = "UPDATE",
                            targetTable = "TASK",
                            targetTableId = taskId.toInt(),
                            description = requestMessage,
                            newData = TaskUpdate(
                                title = taskState.editTitle,
                                description = taskState.description,
                                startDate = taskState.startDate,
                                endDate = taskState.endDate,
                                priority = if (taskState.priority == -1) null else priorityChoice[taskState.priority].uppercase()
                            )
                        )
                        showRequestDialog = true
                    }

                },
                onDeleteClick = {
                    if(isHost == true){
                        showDeleteDialog = true
                    } else {
                        requestMessage = "Send request to delete this task?"
                        requestBody = SendChangeRequest(
                            requestType = "DELETE",
                            targetTable = "TASK",
                            targetTableId = taskId.toInt(),
                            description = requestMessage
                        )
                        showRequestDialog = true
                    }
                },
                enable = taskState.title.isNotEmpty()
            )
        }
    ) { paddingValues ->

        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    focusManager.clearFocus()
                }
                .padding(paddingValues)
                .padding(horizontal = 32.dp)
                .verticalScroll(scrollable),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = taskState.title,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    showTitleEditor = !showTitleEditor
                }
            )

            AnimatedVisibility(visible = showTitleEditor) {
                TextField(
                    value = taskState.editTitle,
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.project_title_icon),
                            contentDescription = ""
                        )
                    },
                    onValueChange = { taskState = taskState.copy(editTitle = it) },
                    placeholder = {
                        Text(
                            text = "Enter task title",
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.clearFocus() },
                        onDone = { focusManager.clearFocus() }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            DropdownStatusSelector(
                text = taskState.status,
                onClick = { showStatusDialog = true },
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                textColor = MaterialTheme.colorScheme.onBackground
            )

            LabeledTextField(
                label = "Description",
                value = taskState.description,
                onValueChange = { taskState = taskState.copy(description = it) },
                placeholder = "Enter project description",
                leadingIconRes = R.drawable.description_icon,
                backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                placeholderColor = MaterialTheme.colorScheme.onBackground,
                textColor = MaterialTheme.colorScheme.onBackground,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() },
                onNext = { focusManager.clearFocus() }
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
                avatarRes = R.drawable.no_avatar,
                userId = taskState.memberId,
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
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() },
                onNext = { focusManager.clearFocus() }
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
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                imeAction = ImeAction.Done,
                onDone = { focusManager.clearFocus() },
                onNext = { focusManager.clearFocus() }
            )

            CommentSection(
                comments = if (comments is Resource.Success) comments?.data!! else emptyList(),
                postComment = { content ->
                    taskDetailViewModel.postComment(
                        projectId,
                        taskId,
                        Content(content),
                        "Bearer ${accessToken.value}"
                    )
                },
                onClick = { comment ->
                    currentComment = comment

                    if (currentComment.author.id == user?.data?.id || isHost == true) {
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

        ChooseItemDialog(
            title = "Choose Member",
            showDialog = showMemberDialog,
            items = members ?: emptyList(),
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
                taskDetailViewModel.updateComment(
                    projectId,
                    taskId,
                    currentComment.id.toString(),
                    Content(content),
                    "Bearer ${accessToken.value}"
                )
                showCommentEditor = false
            },
            onDelete = {
                taskDetailViewModel.deleteComment(
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
                taskDetailViewModel.deleteTask(
                    projectId,
                    taskId,
                    "Bearer ${accessToken.value}"
                )
                showDeleteDialog = false
                onNavigateBack()
            }
        )

        ChangeRequestDialog(
            title = "Send request?",
            content = requestMessage,
            showDialog = showRequestDialog,
            onDismiss = { showRequestDialog = false },
            onConfirm = {
                taskDetailViewModel.createChangeRequest(
                    projectId.toInt(),
                    requestBody,
                    "Bearer ${accessToken.value}"
                )
                showRequestDialog = false
            },
            onDescriptionChange = {
                requestDescription = it
            }
        )

    }


    AlertDialog(
        title = "Update task?",
        content = "Are you sure you want to update this task?",
        showDialog = showSaveDialog,
        onDismiss = {
            showSaveDialog = false
        },
        onConfirm = {

            taskDetailViewModel.updateTaskStatusAndAssignee(
                projectId,
                taskId,
                TaskUpdateStatus(
                    status = statusMapping.firstOrNull { it.first == taskState.status }?.second
                        ?: "PENDING",
                    assigneeId = if(taskState.memberId < 0) null else taskState.memberId
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
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    textColor: Color = MaterialTheme.colorScheme.primary,
    placeholderColor: Color = MaterialTheme.colorScheme.primary,
    imeAction: ImeAction,
    onDone: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
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
            shape = RoundedCornerShape(10.dp),
            textStyle = TextStyle(color = textColor),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onDone = { onDone() },
                onNext = { onNext() }
            )
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
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
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
                   username = comments[index].author.username ?: "Unknown",
                    comment =comments[index].content,
                    userId = comments[index].author.id,
                    createAt = comments[index].createdAt,
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
                    "Enter your comment",
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
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground)
        )
    }
}

@Preview(showBackground = true)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentItem(
    username: String = "Tuong",
    userId: Int = -1,
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
                        id = randomAvatar(userId)
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
fun ButtonBottomBar(
    titleYes: String = "Save",
    titleNo: String = "Delete",
    onSaveClick: () -> Unit,
    enable: Boolean = true,
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
                enabled = enable,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    titleYes, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Button(
                onClick = onDeleteClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    titleNo, fontWeight = FontWeight.Bold, fontSize = 16.sp,
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
                    .shadow(4.dp, RoundedCornerShape(12.dp)) // Sử dụng shadow nhẹ nhàng
                    .animateContentSize(), // Thêm hiệu ứng khi thay đổi kích thước
                shape = RoundedCornerShape(12.dp), // Bo góc mềm mại
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Nền dễ chịu
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp) // Padding bên trong rộng rãi
                ) {
                    Text(
                        text = title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp) // Tạo khoảng cách
                    )

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        maxLines = Int.MAX_VALUE,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface), // Màu chữ dễ đọc
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp)) // Tạo khoảng cách giữa các nút

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Button(
                            onClick = { onSave(comment) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(40.dp) // Đảm bảo nút cao vừa phải
                        ) {
                            Text(
                                "Save",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(40.dp)
                               
                        ) {
                            Text(
                                "Delete",
                                color = MaterialTheme.colorScheme.onError,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}


