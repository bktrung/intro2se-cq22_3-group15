package com.example.youmanage.screens.issue_management

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.issusemanagement.IssueUpdate
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.AssigneeSelector
import com.example.youmanage.screens.components.ChooseItemDialog
import com.example.youmanage.screens.components.DropdownStatusSelector
import com.example.youmanage.screens.components.LeadingTextFieldComponent
import com.example.youmanage.screens.components.TaskSelector
import com.example.youmanage.screens.task_management.primaryColor
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Constants.statusMapping
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.TraceInProjectViewModel
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.issuemanagement.IssueDetailViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun IssueDetailScreen(
    projectId: String = "",
    issueId: String = "",
    onNavigateBack: () -> Unit = {},
    onDisableAction: () -> Unit = {},
    issueDetailViewModel: IssueDetailViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    traceInProjectViewModel: TraceInProjectViewModel = hiltViewModel(),

    ) {
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)

    val members by issueDetailViewModel.members.observeAsState()
    val tasks by issueDetailViewModel.tasks.observeAsState()
    val issue by issueDetailViewModel.issue.observeAsState()
    val issueUpdate by issueDetailViewModel.issueUpdate.observeAsState()

    val shouldDisableAction by traceInProjectViewModel.observeCombinedLiveData(projectId)
        .observeAsState(false)

    var openErrorDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedTask by rememberSaveable { mutableStateOf<Task?>(null) }
    var reporter by remember { mutableStateOf("Unassigned") }
    var reporterId by remember { mutableIntStateOf(-1) }
    var assignedMember by remember { mutableStateOf("Unassigned") }
    var assignedMemberId by remember { mutableIntStateOf(-1) }
    var selectedStatus by rememberSaveable { mutableStateOf("PENDING") }

    var update by remember { mutableStateOf(false) }

    val showChooseTask = remember { mutableStateOf(false) }
    val showChooseMember = remember { mutableStateOf(false) }

    val textFieldColor = MaterialTheme.colorScheme.surface

    val webSocketUrl = "${WEB_SOCKET}project/${projectId}/${projectId}"

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            supervisorScope {
                // Collecting all launched jobs
                val jobs = listOf(
                    launch {
                        try {
                            issueDetailViewModel.loadIssueData(projectId, issueId, "Bearer $token")
                        } catch (e: Exception) {
                            Log.e("IssueManagement", "Error fetching issue: ${e.message}")
                        }
                    },
                    launch {
                        traceInProjectViewModel.connectToWebSocketAndUser(
                            token,
                            webSocketUrl
                        )
                    }
                )

                // Waiting for all tasks to complete
                jobs.joinAll()
            }
        }
    }

    LaunchedEffect(shouldDisableAction){
        if(shouldDisableAction){
            onDisableAction()
        }
    }

    LaunchedEffect(issue) {
        if (issue is Resource.Success) {
            val data = (issue as Resource.Success).data
            Log.d("Data", data.toString())
            title = data?.title ?: "Unknown"
            description = data?.description?:""
            assignedMemberId = data?.assignee?.id ?: -1
            assignedMember = data?.assignee?.username ?: "Unassigned"
            selectedTask = data?.task
            selectedStatus = data?.status ?: "PENDING"
            reporter = data?.reporter?.username ?: "Unassigned"
            reporterId = data?.reporter?.id ?: -1
        }
        if (issue is Resource.Error) {
            openErrorDialog = true
        }
    }

    LaunchedEffect(issueUpdate){
        if (issueUpdate is Resource.Success){
            issueDetailViewModel.getIssue(
                projectId,
                issueId,
                "Bearer ${accessToken.value}"
            )
        }
    }

    LaunchedEffect(update){
        if (update){
            accessToken.value?.let { token ->
                Log.d("Issuse Status", selectedStatus)
                issueDetailViewModel.updateIssue(
                    projectId = projectId,
                    issueId = issue?.data?.id.toString(),
                    IssueUpdate(
                        title = title,
                        description = description,
                        project = projectId.toInt(),
                        assignee = if (assignedMemberId != -1) assignedMemberId else null,
                        task = selectedTask?.id,
                        status = statusMapping.firstOrNull { it.first == selectedStatus }?.second
                            ?: "PENDING",
                    ),
                    authorization = "Bearer $token"
                )
            }
            update = false
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(
            bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
        ),
        topBar = {
            com.example.youmanage.screens.project_management.TopBar(
                title = "Issue Detail",
                onNavigateBack = { onNavigateBack() },
                color = Color.Transparent,
                trailing = {
                    Box(modifier = Modifier.size(24.dp))
                }
            )
        },
        bottomBar = {
            IssueBottomBar(
                enabled = title.isNotEmpty(),
                onSaveClick = {
                    showSaveDialog = true
                },
                onDeleteClick = {
                    showDeleteDialog = true
                }
            )
        }
    ) { paddingValues ->

        val focusManager = LocalFocusManager.current
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { focusManager.clearFocus() }
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Issue Title
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Issue Title",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    LeadingTextFieldComponent(
                        content = title,
                        onChangeValue = { title = it },
                        placeholderContent = "Enter issue title",
                        placeholderColor = Color.Gray,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = R.drawable.project_title_icon,
                        imeAction = ImeAction.Done,
                        onDone = { focusManager.clearFocus() },
                        onNext = { focusManager.clearFocus() }
                    )
                }

                // Description
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Description",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    LeadingTextFieldComponent(
                        content = description,
                        onChangeValue = { description = it },
                        placeholderContent = "Enter issue description",
                        placeholderColor = Color.Gray,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = R.drawable.description_icon,
                        imeAction = ImeAction.Done,
                        onDone = { focusManager.clearFocus() },
                        onNext = { focusManager.clearFocus() }
                    )
                }

                DropdownStatusSelector(
                    text = selectedStatus,
                    onClick = { showStatusDialog = true },
                    backgroundColor = primaryColor,
                    textColor = MaterialTheme.colorScheme.onBackground
                )

                AssigneeSelector(
                    label = "Reporter",
                    avatarRes = R.drawable.no_avatar,
                    userId = reporterId,
                    username = reporter,
                    onClick = { }
                )

                // Choose Task
                TaskSelector(
                    label = "Task",
                    title = selectedTask?.title ?: "Select Task",
                    onClick = { showChooseTask.value = true }
                )

                // Choose Assignee
                AssigneeSelector(
                    label = "Assign to",
                    avatarRes = R.drawable.no_avatar,
                    userId = assignedMemberId,
                    username = assignedMember,
                    onClick = { showChooseMember.value = true }
                )
            }
        }
    }

    if (showChooseTask.value) {
        ChooseItemDialog(
            title = "Choose Task",
            showDialog = showChooseTask.value,
            items = tasks ?: emptyList(),
            displayText = { it.title },
            onDismiss = { showChooseTask.value = false },
            onConfirm = { task ->
                selectedTask = task
                showChooseTask.value = false
            }
        )
    }

    if (showChooseMember.value) {
        ChooseItemDialog(
            title = "Choose Member",
            showDialog = showChooseMember.value,
            items = members ?: emptyList(),
            displayText = { it.username ?: "Unknown" },
            onDismiss = { showChooseMember.value = false },
            onConfirm = {
                assignedMemberId = it.id
                assignedMember = it.username ?: "Unknown"
                showChooseMember.value = false
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
            selectedStatus = it
            showStatusDialog = false
        }
    )

    AlertDialog(
        title = "Update issue?",
        content = "Are you sure you want to update this issue?",
        showDialog = showSaveDialog,
        onDismiss = {
            showSaveDialog = false
        },
        onConfirm = {
            update = true
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
            issueDetailViewModel.deleteIssue(
                projectId,
                issueId,
                "Bearer ${accessToken.value}"
            )
            showDeleteDialog = false
            onNavigateBack()
        }
    )
}


@Composable
fun IssueBottomBar(
    enabled: Boolean = true,
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
                enabled = enabled,
                onClick = onSaveClick,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
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
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
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