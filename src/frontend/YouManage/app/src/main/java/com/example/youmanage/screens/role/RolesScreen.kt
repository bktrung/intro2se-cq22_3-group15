package com.example.youmanage.screens.role

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import com.example.youmanage.data.remote.projectmanagement.Assign
import com.example.youmanage.data.remote.projectmanagement.RoleRequest
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.ChangeRequestDialog
import com.example.youmanage.screens.components.ChooseItemDialog
import com.example.youmanage.screens.components.CreateRoleDialog
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.projectmanagement.ChangeRequestViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.projectmanagement.RoleViewmodel
import com.example.youmanage.viewmodel.TraceInProjectViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


@Composable
fun RolesScreen(
    projectId: String = "1",
    onNavigateBack: () -> Unit = {},
    roleViewmodel: RoleViewmodel = hiltViewModel(),
    onDisableAction: () -> Unit = {},
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    traceInProjectViewModel: TraceInProjectViewModel = hiltViewModel(),
    changeRequestViewModel: ChangeRequestViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val roles by roleViewmodel.roles.observeAsState()
    val response by roleViewmodel.response.observeAsState()

    val shouldDisableAction by traceInProjectViewModel.observeCombinedLiveData(projectId).observeAsState(false)

    val deleteResponse by roleViewmodel.deleteResponse.observeAsState()
    val members by roleViewmodel.members.observeAsState()
    val isHost by projectManagementViewModel.isHost.observeAsState()
    val responseRequest by changeRequestViewModel.response.observeAsState()

    val context = LocalContext.current

    var showCreateRoleDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateRoleDialog by remember { mutableStateOf(false) }
    var showAssignRoleDialog by remember { mutableStateOf(false) }
    var showRequestDialog by remember { mutableStateOf(false) }

    var roleName by rememberSaveable { mutableStateOf("") }
    var roleDescription by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableIntStateOf(-1) }
    var requestMessage by rememberSaveable { mutableStateOf("") }

    var requestSelection by remember { mutableIntStateOf(-1) }

    val webSocketUrl = "${WEB_SOCKET}project/$projectId/"

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            supervisorScope {
                // Launching tasks concurrently
                val job1 = launch {
                    traceInProjectViewModel.connectToWebSocketAndUser(
                        token,
                        webSocketUrl
                    )
                }
                val job2 = launch {
                    roleViewmodel.getRoles(
                        projectId,
                        "Bearer $token"
                    )
                }

                val job3 = launch {
                    projectManagementViewModel.isHost(
                        projectId,
                        "Bearer $token"
                    )
                }

                // Waiting for both jobs to complete
                joinAll(job1, job2, job3)
            }
        }
    }

    LaunchedEffect(shouldDisableAction){
        if(shouldDisableAction){
            onDisableAction()
        }
    }

    LaunchedEffect(
        key1 = response,
        key2 = deleteResponse
    ) {
        supervisorScope {
            val job1 =launch {
                if (response is Resource.Success) {
                    roleViewmodel.getRoles(
                        projectId,
                        "Bearer ${accessToken.value}"
                    )
                }
            }

            val job2 = launch {
                if (deleteResponse is Resource.Success) {
                    roleViewmodel.getRoles(
                        projectId,
                        "Bearer ${accessToken.value}"
                    )
                }
            }

            job1.join()
            job2.join()
        }

    }


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),

        topBar = {
            TopBar(
                title = "Roles",
                trailing = {
                    Spacer(modifier = Modifier.size(24.dp))
                },
                color = Color.Transparent,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        requestSelection = 1
                        showCreateRoleDialog = true
                    },
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.border(
                        2.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(10.dp)
                    )
                ) {
                    Text(
                        "Create Role",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    ) { paddingValues ->

        when (roles) {
            is Resource.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(vertical = 20.dp)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    items(roles?.data?.size ?: 0) {
                        RoleItem(
                            name = roles?.data?.get(it)?.name ?: "",
                            onDelete = {
                                selectedRole = roles?.data?.get(it)?.id ?: -1
                                if(isHost == true){
                                    showDeleteDialog = true
                                } else {
                                    requestSelection = 3
                                    requestMessage = "Send request to delete this role?"
                                    showRequestDialog = true
                                }

                            },
                            onUpdate = {
                                requestSelection = 2
                                selectedRole = roles?.data?.get(it)?.id ?: -1
                                showUpdateRoleDialog = true
                            },
                            onAssign = {
                                if (isHost == true) {
                                    showAssignRoleDialog = true
                                    selectedRole = roles?.data?.get(it)?.id ?: -1
                                    roleViewmodel.getListMemberOfRole(
                                        projectId,
                                        selectedRole.toString(),
                                        "Bearer ${accessToken.value}"
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Only host can assign role",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }

            is Resource.Error -> {
                Text(
                    text = roles?.message.toString(),
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .padding(horizontal = 20.dp)
                )
            }

            is Resource.Loading -> {
                CircularProgressIndicator()
            }

            null -> {}
        }

    }

    CreateRoleDialog(
        onDismiss = {
            showCreateRoleDialog = false
        },
        showDialog = showCreateRoleDialog,
        onConfirm = {
            if (isHost == true) {
                roleViewmodel.createRole(
                    projectId,
                    role = RoleRequest(
                        roleName,
                        roleDescription
                    ),
                    "Bearer ${accessToken.value}"
                )
            } else {
                requestMessage = "Send request to create this role?"
                showRequestDialog = true
            }

            showCreateRoleDialog = false
        },

        onNameChange = {
            roleName = it
        },
        onDescriptionChange = {
            roleDescription = it
        }
    )

    CreateRoleDialog(
        title = "Update Role",
        showDialog = showUpdateRoleDialog,
        nameOg = roles?.data?.find { it.id == selectedRole }?.name ?: "",
        descriptionOg = roles?.data?.find { it.id == selectedRole }?.description ?: "",
        onDismiss = {
            showUpdateRoleDialog = false
        },
        onConfirm = {
            if (isHost == true) {
                roleViewmodel.updateRole(
                    projectId,
                    selectedRole.toString(),
                    role = RoleRequest(
                        roleName,
                        roleDescription
                    ),
                    "Bearer ${accessToken.value}"
                )
            } else {
                requestMessage = "Send request to update this role?"
                showRequestDialog = true
            }

            showUpdateRoleDialog = false
        },

        onNameChange = {
            roleName = it
        },
        onDescriptionChange = {
            roleDescription = it
        }
    )

    AlertDialog(
        title = "Delete this role?",
        content = "Are you sure you want to delete this role?",
        showDialog = showDeleteDialog,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            roleViewmodel.deleteRole(
                projectId,
                selectedRole.toString(),
                "Bearer ${accessToken.value}"
            )
            showDeleteDialog = false
        }
    )

    ChooseItemDialog(
        showDialog = showAssignRoleDialog,
        title = "Assign to",
        isReset = true,
        items = members?.flatMap { it.keys }?.toList() ?: emptyList(),
        displayText = { it.username ?: "Unknown" },
        checkItems = members?.flatMap { it.values }?.toList() ?: emptyList(),
        onDismiss = { showAssignRoleDialog = false },
        onConfirm = {

            roleViewmodel.assignRole(
                projectId,
                selectedRole.toString(),
                Assign(it.id),
                "assign",
                "Bearer ${accessToken.value}"
            )

            showAssignRoleDialog = false
        }
    )
    var requestDescription by remember { mutableStateOf("") }

    ChangeRequestDialog(
        title = "Send Request?",
        content = requestMessage,
        showDialog = showRequestDialog,
        onDismiss = { showRequestDialog = false },
        onConfirm = {
            showRequestDialog = false
            if(requestSelection == 1) {
                changeRequestViewModel.createChangeRequest(
                    projectId = projectId.toInt(),
                    changeRequest = SendChangeRequest(
                        requestType = "CREATE",
                        targetTable = "ROLE",
                        targetTableId = null,
                        description = requestMessage,
                        newData = RoleRequest(
                            roleName,
                            roleDescription
                        )
                    ),
                    authorization = "Bearer ${accessToken.value}"
                )
                showRequestDialog = false
            } else if(requestSelection == 2) {
                changeRequestViewModel.createChangeRequest(
                    projectId = projectId.toInt(),
                    changeRequest = SendChangeRequest(
                        requestType = "UPDATE",
                        targetTable = "ROLE",
                        targetTableId = selectedRole,
                        description = requestMessage,
                        newData = RoleRequest(
                            roleName,
                            roleDescription
                        )
                    ),
                    authorization = "Bearer ${accessToken.value}"
                )
                showRequestDialog = false
            } else if(requestSelection == 3) {
                changeRequestViewModel.createChangeRequest(
                    projectId = projectId.toInt(),
                    changeRequest = SendChangeRequest(
                        requestType = "DELETE",
                        targetTable = "ROLE",
                        targetTableId = selectedRole,
                        description = requestMessage,
                        newData = null
                    ),
                    authorization = "Bearer ${accessToken.value}"
                )
                showRequestDialog = false
            }

            requestSelection = -1

        },
        onDescriptionChange = { requestDescription = it}
    )


}


@Composable
fun RoleItem(
    name: String = "",
    onDelete: () -> Unit = {},
    onUpdate: () -> Unit = {},
    onAssign: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                ),
                maxLines = Int.MAX_VALUE,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Row {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(30.dp)
                        .clickable { onUpdate() },
                    tint = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(30.dp)
                        .clickable { onDelete() },
                    tint = MaterialTheme.colorScheme.primary
                )

                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Assign",
                    modifier = Modifier
                        .padding(5.dp)
                        .size(30.dp)
                        .clickable { onAssign() },
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

