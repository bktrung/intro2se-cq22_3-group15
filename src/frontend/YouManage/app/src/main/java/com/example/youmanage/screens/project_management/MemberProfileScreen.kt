package com.example.youmanage.screens.project_management

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.data.remote.projectmanagement.Assign
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.randomAvatar
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.RoleViewmodel

@Preview
@Composable
fun MemberProfileScreen(
    projectId: String = "",
    memberId: String = "",
    onNavigateBack: () -> Unit = {},
    roleViewmodel: RoleViewmodel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    onDisableAction: () -> Unit = {}
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val user by authenticationViewModel.user.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val project by projectManagementViewModel.project.observeAsState()
    val roles by roleViewmodel.roles.observeAsState()
    val unAssignResponse by roleViewmodel.assignResponse.observeAsState()

    var showUnAssignDialog by remember { mutableStateOf(false) }
    var isSelectedRole by remember { mutableIntStateOf(-1) }

    val webSocketUrl = "${WEB_SOCKET}project/$projectId/"

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            projectManagementViewModel.getProject(projectId, "Bearer $token")
            projectManagementViewModel.connectToProjectWebsocket(webSocketUrl)
            projectManagementViewModel.connectToMemberWebsocket(webSocketUrl)
            roleViewmodel.getRolesOfMember(projectId, memberId, "Bearer $token")
        }
    }

    HandleOutProjectWebSocket(
        memberSocket = memberSocket,
        projectSocket = projectSocket,
        user = user,
        projectId = projectId,
        onDisableAction = onDisableAction
    )

    var member by remember { mutableStateOf(User()) }

    LaunchedEffect(project) {
        if (project is Resource.Success) {
            member = project?.data?.members?.find { it.id == memberId.toInt() } ?: User()
        }
    }

    LaunchedEffect(unAssignResponse) {
        if (unAssignResponse is Resource.Success) {
            roleViewmodel.getRolesOfMember(projectId, memberId, "Bearer ${accessToken.value}")
        }
    }

    // Implement the UI for the member profile screen
    Scaffold(
        topBar = {
            TopBar(
                title = "Member Profile",
                onNavigateBack = onNavigateBack,
                color = Color.Transparent,
                trailing = {
                   Box(
                       modifier = Modifier.size(24.dp)
                   )
                }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            )

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .padding(16.dp)
        ) {


            Image(
                painter = painterResource(randomAvatar(member.id)),
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Member Username",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = member.username ?: "",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Email",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = member.email,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Roles",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .padding(horizontal = 20.dp)
                    .height(500.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                itemsIndexed(roles?.data ?: emptyList()) { index, item ->
                    MemberRoleItem(
                        name = item.name,
                        onUnAssign = {
                            isSelectedRole = item.id
                            showUnAssignDialog = true
                        }
                    )
                }
            }
        }
    }

    AlertDialog(
        title = "Unassign this role?",
        content = "Are you sure you want to unassign this role?",
        showDialog = showUnAssignDialog,
        onDismiss = { showUnAssignDialog = false },
        onConfirm = {
            accessToken.value?.let { token ->
                if (isSelectedRole != -1) {
                    roleViewmodel.assignRole(
                        projectId = projectId,
                        roleId = isSelectedRole.toString(),
                        member = Assign(userId = member.id),
                        action = "unassign",
                        authorization = "Bearer $token"
                    )
                }
            }
            showUnAssignDialog = false
        }
    )
}

@Composable
fun MemberRoleItem(
    name: String = "",
    onUnAssign: () -> Unit = {}
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
            containerColor = Color(0xffBAE5F5)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = Color.Black
                ),
                maxLines = Int.MAX_VALUE,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Row {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Assign",
                    modifier = Modifier


                        .padding(5.dp)
                        .size(30.dp)
                        .clickable { onUnAssign() },
                    tint = Color.Black
                )
            }
        }
    }
}

