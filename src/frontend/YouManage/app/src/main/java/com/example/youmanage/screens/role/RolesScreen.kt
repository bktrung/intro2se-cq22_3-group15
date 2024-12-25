package com.example.youmanage.screens.role

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.data.remote.projectmanagement.Assign
import com.example.youmanage.data.remote.projectmanagement.RoleRequest
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.ChooseItemDialog
import com.example.youmanage.screens.components.CreateRoleDialog
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.RoleViewmodel


@Composable
fun RolesScreen(
    projectId: String = "1",
    onNavigateBack: () -> Unit = {},
    roleViewmodel: RoleViewmodel = hiltViewModel(),
    onDisableAction: () -> Unit = {},
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val user by authenticationViewModel.user.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val roles by roleViewmodel.roles.observeAsState()
    val response by roleViewmodel.response.observeAsState()
    val deleteResponse by roleViewmodel.deleteResponse.observeAsState()
    val members by roleViewmodel.members.observeAsState()

    var showCreateRoleDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showUpdateRoleDialog by remember { mutableStateOf(false) }
    var showAssignRoleDialog by remember { mutableStateOf(false) }


    val context = LocalContext.current

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            roleViewmodel.getRoles(
                projectId,
                "Bearer $token"
            )
        }
    }

    LaunchedEffect(
        key1 = response,
        key2 = deleteResponse
    ) {
        if (response is Resource.Success) {
            roleViewmodel.getRoles(
                projectId,
                "Bearer ${accessToken.value}"
            )
        }

        if (deleteResponse is Resource.Success) {
            roleViewmodel.getRoles(
                projectId,
                "Bearer ${accessToken.value}"
            )
        }
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


    var roleName by rememberSaveable { mutableStateOf("") }
    var roleDescription by rememberSaveable { mutableStateOf("") }
    var selectedRole by rememberSaveable { mutableIntStateOf(-1) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        topBar = {
            com.example.youmanage.screens.project_management.TopBar(
                title = "Roles",
                trailing = {
                    Spacer(modifier = Modifier.size(24.dp))
                },
                color = MaterialTheme.colorScheme.primaryContainer,
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
                        showCreateRoleDialog = true
                    },
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .border(
                            2.dp,
                            Color.Black,
                            RoundedCornerShape(30.dp)
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
                                showDeleteDialog = true
                            },
                            onUpdate = {
                                selectedRole = roles?.data?.get(it)?.id ?: -1
                                showUpdateRoleDialog = true
                            },
                            onAssign = {
                                showAssignRoleDialog = true
                                selectedRole = roles?.data?.get(it)?.id ?: -1
                                roleViewmodel.getListMemberOfRole(
                                    projectId,
                                    selectedRole.toString(),
                                    "Bearer ${accessToken.value}"
                                )
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
            roleViewmodel.createRole(
                projectId,
                role = RoleRequest(
                    roleName,
                    roleDescription
                ),
                "Bearer ${accessToken.value}"
            )
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
            roleViewmodel.updateRole(
                projectId,
                selectedRole.toString(),
                role = RoleRequest(
                    roleName,
                    roleDescription
                ),
                "Bearer ${accessToken.value}"
            )

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
    Log.d("selectedRole", members?.flatMap { it.keys }?.toList().toString())
    Log.d("selectedRole", members?.flatMap { it.values }?.toList().toString())

    ChooseItemDialog(
        showDialog = showAssignRoleDialog,
        title = "Assign to",
        isReset = true,
        items = members?.flatMap { it.keys }?.toList() ?: emptyList(),
        displayText = { it.username },
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

