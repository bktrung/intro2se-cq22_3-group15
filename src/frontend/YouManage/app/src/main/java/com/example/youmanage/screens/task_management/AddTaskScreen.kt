package com.example.youmanage.screens.task_management

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.screens.components.AssigneeSelector
import com.example.youmanage.screens.components.ChangeRequestDialog
import com.example.youmanage.screens.components.ChooseItemDialog
import com.example.youmanage.screens.components.DatePickerField
import com.example.youmanage.screens.components.DatePickerModal
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.screens.components.LeadingTextFieldComponent
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Constants.priorityChoice
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.viewmodel.projectmanagement.ChangeRequestViewModel
import com.example.youmanage.viewmodel.SnackBarViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


@Preview
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTaskScreen(
    projectId: String = "",
    onCreateTask: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onDisableAction: () -> Unit = {},
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    changeRequestViewModel: ChangeRequestViewModel = hiltViewModel(),
    showSnackBarViewModel: SnackBarViewModel = hiltViewModel()
) {
    // Data from API
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val user by authenticationViewModel.user.observeAsState()
    val members by taskManagementViewModel.members.observeAsState()
    val task by taskManagementViewModel.task.observeAsState()
    val isHost by taskManagementViewModel.isHost.observeAsState()
    val changeRequestResponse by changeRequestViewModel.response.observeAsState()

    // WebSocket
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()

    var send by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var openErrorDialog by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(task) {
        if (task is Resource.Success && !isLoading) {
            onCreateTask()
        } else if (task is Resource.Error) {
            errorMessage = (task as Resource.Error).message ?: "Something went wrong"
            openErrorDialog = true
        }
    }

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val webSocketUrl = "${WEB_SOCKET}project/${projectId}/"
            supervisorScope {
                // Track all async tasks with Deferred
                val job1 = launch { taskManagementViewModel.getMembers(projectId, "Bearer $token") }
                val job2 = launch { taskManagementViewModel.isHost(projectId, "Bearer $token") }
                val job3 = launch { authenticationViewModel.getUser("Bearer $token") }
                val job4 =
                    launch { taskManagementViewModel.connectToTaskWebSocket(url = webSocketUrl) }
                val job5 =
                    launch { projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl) }
                val job6 =
                    launch { projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl) }

                // Wait for all jobs to complete
                joinAll(job1, job2, job3, job4, job5, job6)
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

    var showDatePicker by remember { mutableStateOf(false) }
    val textFieldColor = MaterialTheme.colorScheme.surface

    var showChooseMember by remember { mutableStateOf(false) }
    var showChangeRequestDialog by remember { mutableStateOf(false) }

    var isTime by rememberSaveable { mutableIntStateOf(0) }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableIntStateOf(-1) }

    var assignedMemberId by remember { mutableIntStateOf(-1) }
    var assignedMember by remember { mutableStateOf("Unassigned") }

    var requestDescription by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(changeRequestResponse) {
        if (changeRequestResponse is Resource.Success) {
            Toast.makeText(context, "Request sent successfully!", Toast.LENGTH_SHORT).show()
        } else if (changeRequestResponse is Resource.Error) {
            Toast.makeText(context, "Something went wrong. Try again!", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(send) {
        if (send) {
            supervisorScope {
                launch {
                    changeRequestViewModel.createChangeRequest(
                        projectId = projectId.toInt(),
                        SendChangeRequest(
                            requestType = "CREATE",
                            targetTable = "TASK",
                            targetTableId = null,
                            description = requestDescription,
                            newData = TaskCreate(
                                title = title,
                                description = description,
                                startDate = startDate,
                                endDate = endDate,
                                assigneeId = if (assignedMemberId == -1) null else assignedMemberId,
                                priority = if (priority == -1) null else priorityChoice[priority].uppercase(),
                            )
                        ),
                        "Bearer ${accessToken.value}"
                    )
                }
            }
        }
    }


        Scaffold(
            topBar = {
                TopBar(
                    title = "Create Task",
                    color = Color.Transparent,
                    trailing = {
                        Box(
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    onNavigateBack = { onNavigateBack() }
                )
            },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (isHost == false) {
                                showChangeRequestDialog = true
                            } else {
                                taskManagementViewModel.createTask(
                                    projectId = projectId,
                                    TaskCreate(
                                        title = title,
                                        description = description,
                                        startDate = startDate,
                                        endDate = endDate,
                                        assigneeId = if (assignedMemberId == -1) null else assignedMemberId,
                                        priority = if (priority == -1) null else priorityChoice[priority].uppercase(),
                                    ),
                                    authorization = "Bearer ${accessToken.value}"
                                )
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Text(
                            "Create",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars
                        .asPaddingValues()
                        .calculateTopPadding(),
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )
        ) { paddingValues ->

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(horizontal = 36.dp)
                    .padding(bottom = 10.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Task Title",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    LeadingTextFieldComponent(
                        content = title,
                        onChangeValue = { title = it },
                        placeholderContent = "Enter project title",
                        placeholderColor = MaterialTheme.colorScheme.primary,
                        containerColor = textFieldColor,
                        icon = R.drawable.project_title_icon
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Description",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = textFieldColor,
                            unfocusedContainerColor = textFieldColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.description_icon),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        placeholder = {
                            Text(
                                "Enter project description",
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        maxLines = Int.MAX_VALUE,
                        shape = RoundedCornerShape(10.dp),
                        textStyle = TextStyle(MaterialTheme.colorScheme.primary)
                    )
                }

                DatePickerField(
                    label = "Start date",
                    date = startDate,
                    onDateClick = {
                        isTime = 1
                        showDatePicker = true
                    },
                    iconResource = R.drawable.calendar_icon,
                    placeholder = "Enter start date",
                    containerColor = textFieldColor
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
                    containerColor = textFieldColor
                )

                PrioritySelector(
                    priorityChoice = priorityChoice,
                    priority = priority,
                    onPrioritySelected = {
                        priority = it
                    }
                )

                AssigneeSelector(
                    label = "Assign to",
                    avatarRes = R.drawable.no_avatar,
                    userId = assignedMemberId,
                    username = assignedMember,
                    onClick = {
                        showChooseMember = true
                    }
                )
            }
    }

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


    ChangeRequestDialog(
        title = "Send Request?",
        content = "Send request to host to create this task?",
        showDialog = showChangeRequestDialog,
        onDismiss = { showChangeRequestDialog = false },
        onDescriptionChange = { requestDescription = it },
        onConfirm = {
            send = true

            showChangeRequestDialog = false
        }
    )


    ChooseItemDialog(
        title = "Choose Member",
        showDialog = showChooseMember,
        items = members ?: emptyList(),
        displayText = { it.username ?: "Unassigned" },
        onDismiss = { showChooseMember = false },
        onConfirm = {
            assignedMemberId = it.id
            assignedMember = it.username ?: "Unassigned"
            showChooseMember = false
        }
    )

    ErrorDialog(
        title = "Something wrong?",
        content = errorMessage,
        showDialog = openErrorDialog,
        onDismiss = { openErrorDialog = false },
        onConfirm = { openErrorDialog = false }
    )
}

@Composable
fun PrioritySelector(
    priorityChoice: List<String>,
    priority: Int,
    onPrioritySelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Priority",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            priorityChoice.forEachIndexed { index, item ->
                Button(
                    onClick = {
                        // Toggle the state if the same button is clicked twice
                        onPrioritySelected(if (index == priority) -1 else index)
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        if (index == priority) MaterialTheme.colorScheme.primary else Color.Transparent
                    ),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = item,
                        color = if (index != priority) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
