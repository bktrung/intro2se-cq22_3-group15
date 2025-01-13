package com.example.youmanage.screens.project_management

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.DatePickerModal
import com.example.youmanage.screens.components.LeadingTextFieldComponent
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.common.ProjectManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UpdateProjectScreen(
    projectId: String,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onDisableAction: () -> Unit
) {



    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val project by projectManagementViewModel.project.observeAsState()
    val updateResponse by projectManagementViewModel.updateProjectResponse.observeAsState()
    val user by authenticationViewModel.user.observeAsState()

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val bearerToken = "Bearer $token"
            val webSocketUrl = "${WEB_SOCKET}project/$projectId/"

            supervisorScope {
                // Launch all tasks concurrently
                val jobs = listOf(
                    launch {
                        try {
                            authenticationViewModel.getUser(bearerToken)
                        } catch (e: Exception) {
                            Log.e("Authentication", "Error fetching user: ${e.message}")
                        }
                    },
                    launch {
                        try {
                            projectManagementViewModel.getProject(projectId, bearerToken)
                        } catch (e: Exception) {
                            Log.e("ProjectManagement", "Error fetching project: ${e.message}")
                        }
                    },
                    launch {
                        try {
                            projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl)
                        } catch (e: Exception) {
                            Log.e("WebSocket", "Error connecting to project WebSocket: ${e.message}")
                        }
                    },
                    launch {
                        try {
                            projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl)
                        } catch (e: Exception) {
                            Log.e("WebSocket", "Error connecting to member WebSocket: ${e.message}")
                        }
                    }
                )

                // Wait for all tasks to complete before continuing
                jobs.joinAll()
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
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }

    var members by remember { mutableStateOf(listOf<MemberItem>()) }

    val textFieldColor = Color(0xFFF5F5F5)

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var dueDate by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(project) {
        if (project is Resource.Success) {
            title = project?.data?.name.toString()
            description = project?.data?.description.toString()
            dueDate = project?.data?.dueDate.toString()
        }
    }

    val context = LocalContext.current

    LaunchedEffect(updateResponse) {
        if (updateResponse is Resource.Success) {
            supervisorScope {
                launch {
                    projectManagementViewModel.getProject(
                        id = projectId,
                        authorization = "Bearer ${accessToken.value}"
                    )
                }
            }
            Toast.makeText(context, "Updated Successfully!", Toast.LENGTH_SHORT).show()
        } else if(updateResponse is Resource.Error) {

            Toast.makeText(context, "Updated Failed!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Update Project",
                onNavigateBack = onNavigateBack,
                trailing = {
                    Box(modifier = Modifier.size(24.dp))
                },
                color = Color.Transparent
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues())

    ) { paddingValues ->
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Project Title",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    LeadingTextFieldComponent(
                        content = title,
                        onChangeValue = { title = it },
                        placeholderContent = "Enter project title",
                        placeholderColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        icon = R.drawable.project_title_icon,
                        imeAction = ImeAction.Next,
                        onDone = { focusManager.moveFocus(FocusDirection.Down)},
                        onNext = {  focusManager.moveFocus(FocusDirection.Down)}
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
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
                            focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.description_icon),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        placeholder = {
                            Text(
                                "Enter project description",
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.clearFocus() },
                            onDone = { focusManager.clearFocus() }
                        ),
                        maxLines = Int.MAX_VALUE,
                        shape = RoundedCornerShape(10.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Due date",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Box(
                        modifier = Modifier.clickable {
                            showDatePicker = true
                        }
                    ) {
                        TextField(
                            value = dueDate,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.calendar_icon
                                    ),
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.clickable {
                                        showDatePicker = true
                                    }
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Enter due date",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = { showUpdateDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()

                    ) {
                        Text(
                            "Update",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                    }


                }

            }
        }
    }
    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = {
                dueDate = it
            },
            onDismiss = {
                showDatePicker = false
            })
    }

    val independentScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    AlertDialog(
        title = "Update Project",
        content = "Are you sure you want to update this project?",
        showDialog = showUpdateDialog,
        onDismiss = { showUpdateDialog = false },
        onConfirm = {
            independentScope.launch {
                try {
                    projectManagementViewModel.updateProject(
                        id = projectId,
                        project = ProjectCreate(
                            name = title,
                            description = description,
                            dueDate = dueDate
                        ),
                        authorization = "Bearer ${accessToken.value}"
                    )
                } catch (e: Exception) {
                    Log.e("UpdateProject", "Failed to update project: ${e.message}")
                } finally {
                    // Đảm bảo dialog được tắt bất kể thành công hay lỗi
                    withContext(Dispatchers.Main) {
                        showUpdateDialog = false
                    }
                }
            }
        }

    )
}