package com.example.youmanage.screens.issue_management

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.issusemanagement.IssueCreate
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.screens.components.AssigneeSelector
import com.example.youmanage.screens.components.ChooseItemDialog
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.screens.components.LeadingTextFieldComponent
import com.example.youmanage.screens.components.TaskSelector
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.IssuesViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddIssueScreen(
    projectId: String = "",
    onIssueCreated: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    issueManagementViewModel: IssuesViewModel = hiltViewModel(),
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel()
) {
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val members by projectManagementViewModel.members.observeAsState()
    val tasks by taskManagementViewModel.tasks.observeAsState()
    val issue by issueManagementViewModel.issue.observeAsState()
    var openErrorDialog by remember { mutableStateOf(false) }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedTask by rememberSaveable { mutableStateOf<Task?>(null) }
    var assignedMemberId by remember { mutableIntStateOf(-1) }
    var assignedMember by remember { mutableStateOf("Unassigned") }

    val showChooseTask = remember { mutableStateOf(false) }
    val showChooseMember = remember { mutableStateOf(false) }

    val backgroundColor = MaterialTheme.colorScheme.background
    val textFieldColor = MaterialTheme.colorScheme.surface

    LaunchedEffect(issue) {
        if (issue is Resource.Success) {
            onIssueCreated()
        }
        if (issue is Resource.Error) {
            openErrorDialog = true
        }
    }
    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            projectManagementViewModel.getMembers(projectId, "Bearer $token")
            taskManagementViewModel.getTasks(projectId, "Bearer $token")
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Create Issue",
                onNavigateBack = { onNavigateBack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
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

                    LeadingTextFieldComponent(
                        content = description,
                        onChangeValue = { description = it },
                        placeholderContent = "Enter issue description",
                        placeholderColor = MaterialTheme.colorScheme.primary,
                        containerColor = textFieldColor,
                        icon = R.drawable.description_icon
                    )
                }



//             Choose Task
                TaskSelector(
                    label = "Task",
                    title = selectedTask?.title ?: "Select Task",
                    onClick = { showChooseTask.value = true }
                )

                // Choose Assignee
                AssigneeSelector(
                    label = "Assign to",
                    avatarRes = R.drawable.avatar,
                    username = assignedMember,
                    onClick = { showChooseMember.value = true }
                )

                Button(
                    onClick = {
                        accessToken.value?.let { token ->
                            issueManagementViewModel.createIssue(
                                projectId = projectId,
                                IssueCreate(
                                    title = title,
                                    description = description,
                                    project = projectId.toInt(),
                                    assignee = if (assignedMemberId != -1) assignedMemberId else null,
                                    task = selectedTask?.id
                                ),
                                authorization = "Bearer $token"
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()


                ) {
                    Text(
                        "Create Issue",
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                }
            }
        }
    }


//     Dialogs for choosing task and member
    if (showChooseTask.value) {
        ChooseItemDialog(
            title = "Choose Task",
            showDialog = showChooseTask.value,
            items = tasks?.data ?: emptyList(),
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
            items = members?.data ?: emptyList(),
            displayText = { it.username },
            onDismiss = { showChooseMember.value = false },
            onConfirm = { user ->
                assignedMemberId = user.id
                assignedMember = user.username
                showChooseMember.value = false
            }
        )
    }

    // Error Dialog
    if (openErrorDialog) {
        ErrorDialog(
            title = "Error",
            content = "Failed to create issue. Please try again.",
            showDialog = openErrorDialog,
            onDismiss = { openErrorDialog = false },
            onConfirm = { openErrorDialog = false }
        )
    }
}
