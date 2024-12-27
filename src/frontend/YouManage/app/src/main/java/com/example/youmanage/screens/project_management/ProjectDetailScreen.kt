package com.example.youmanage.screens.project_management

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.Host
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Username
import com.example.youmanage.screens.components.AddMemberDialog
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.PieChart
import com.example.youmanage.screens.components.PieChartInput
import com.example.youmanage.screens.components.pieChartInput
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel
import kotlinx.coroutines.delay

@Composable
fun ProjectDetailScreen(
    backgroundColor: Color = Color(0xffBAE5F5),
    id: Int,
    onNavigateBack: () -> Unit,
    onClickMenu: () -> Unit,
    onDisableAction: () -> Unit,
    onUpdateProject: () -> Unit,
    onMemberProfile: (Int) -> Unit,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel()
) {
    val project by projectManagementViewModel.project.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val addMemberResponse by projectManagementViewModel.addMemberResponse.observeAsState()
    val removeMemberResponse by projectManagementViewModel.deleteMemberResponse.observeAsState()
    val projectProgress by projectManagementViewModel.progress.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val taskSocket by taskManagementViewModel.taskSocket.observeAsState()

    val user by authenticationViewModel.user.observeAsState()

    var pieChartInputList by remember { mutableStateOf<List<PieChartInput>>(emptyList()) }

    var showAddMemberDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveAlertDialog by rememberSaveable { mutableStateOf(false) }
    var showAddAlertDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var isRemove by remember { mutableStateOf(false) }
    var isAdd by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(accessToken.value)
    {
        accessToken.value?.let { token ->
            val authorization = "Bearer $token"

            projectManagementViewModel.getProject(
                id = id.toString(),
                authorization = authorization
            )

            projectManagementViewModel.getProgressTrack(
                id = id.toString(),
                authorization = authorization
            )

        }
    }

    LaunchedEffect(addMemberResponse) {
        if (addMemberResponse is Resource.Error && isAdd) {
            showAddAlertDialog = true
        }

        if (addMemberResponse is Resource.Success) {
            projectManagementViewModel.getProject(
                id = id.toString(),
                authorization = "Bearer ${accessToken.value}"
            )
        }
    }

    LaunchedEffect(removeMemberResponse) {
        if (removeMemberResponse is Resource.Error && isRemove) {
            showRemoveAlertDialog = true
        }

        if (removeMemberResponse is Resource.Success) {
            projectManagementViewModel.getProject(
                id = id.toString(),
                authorization = "Bearer ${accessToken.value}"
            )
        }
    }

    LaunchedEffect(accessToken.value)
    {
        accessToken.value?.let {
            val webSocketUrl = "${WEB_SOCKET}project/$id/"
            authenticationViewModel.getUser("Bearer $it")
            projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl)
            projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl)
            taskManagementViewModel.connectToTaskWebSocket(url = webSocketUrl)
        }
    }

    HandleOutProjectWebSocket(
        memberSocket = memberSocket,
        projectSocket = projectSocket,
        user = user,
        projectId = id.toString(),
        onDisableAction = onDisableAction
    )

    LaunchedEffect(projectSocket) {
        if (projectSocket is Resource.Success &&
            projectSocket?.data?.type == "project_updated" &&
            projectSocket?.data?.content?.id == id
        ) {
            projectManagementViewModel.getProject(
                id.toString(),
                "Bearer ${accessToken.value}"
            )
        }
    }

    val taskStage = listOf(
        "task_created",
        "task_updated",
        "task_deleted"
    )

    LaunchedEffect(taskSocket) {
        if (taskSocket is Resource.Success &&
            taskStage.contains(taskSocket?.data?.type) &&
            projectSocket?.data?.content?.id == id
        ) {
            projectManagementViewModel.getProgressTrack(
                id = id.toString(),
                authorization = "Bearer ${accessToken.value}"
            )
        }
    }

    val memberStage = listOf(
        "member_added",
        "member_removed"
    )

    LaunchedEffect(memberSocket) {
        if (memberSocket is Resource.Success &&
            memberStage.contains(memberSocket?.data?.type) &&
            projectSocket?.data?.content?.id == id
        ) {
            projectManagementViewModel.getProject(id.toString(), "Bearer ${accessToken.value}")
        }
    }

    LaunchedEffect(projectProgress) {
        if (projectProgress is Resource.Success) {

            var total = projectProgress?.data?.total ?: 1
            var pending = projectProgress?.data?.pending ?: 0
            val inProgress = projectProgress?.data?.inProgress ?: 0
            val completed = projectProgress?.data?.completed ?: 0

            if (total == 0) {
                total = 1
                pending = 1
            }

            pieChartInputList = listOf(
                PieChartInput(
                    color = Color(0xffFFD580),
                    value = pending.toDouble().div(total) * 100.0,
                    description = "Pending"
                ),
                PieChartInput(
                    color = Color(0xff90CAF9),
                    value = inProgress.toDouble().div(total) * 100.0,
                    description = "In Progress"
                ),
                PieChartInput(
                    color = Color(0xffA5D6A7),
                    value = completed.toDouble().div(total) * 100.0,
                    description = "Completed"
                )
            )
        } else if (projectProgress is Resource.Error) {
            Log.d("Progress Tracker Error", projectProgress?.message.toString())
        }
    }

    Log.d("Access Token", "${accessToken.value}")
    Log.d("Project ID", "$id")

    if (project is Resource.Success) {

        var memberId by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopBar(
                    "Project Detail",
                    trailing = {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                val userId = user?.data?.id ?: -1
                                val hostId = project?.data?.host?.id ?: -2
                                if(userId == hostId){
                                    onUpdateProject()
                                } else{
                                    Toast.makeText(context, "You are not the host of this project", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Update"
                                )

                            }
                            IconButton(onClick = { onClickMenu() }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }

                    },
                    color = Color.Transparent,
                    onNavigateBack = { onNavigateBack() }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )

        ) { paddingValues ->

            val scrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Project Name",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = project?.data?.name.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (projectProgress is Resource.Success) {
                        PieChart(
                            input = pieChartInputList,
                            modifier = Modifier.padding(36.dp)
                        )
                    }

                    DescriptionSection(
                        description = project?.data?.description.toString()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Due date",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = project?.data?.dueDate.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    MembersSection(
                        onAddNewMember = {
                            showAddMemberDialog = true
                        },

                        onDeleteMember = {
                            memberId = it
                            showDeleteDialog = true
                        },
                        members = project?.data?.members ?: emptyList(),
                        onMemberProfile = onMemberProfile,
                        hostId = project?.data?.host?.id ?: -1,
                        isHost = user?.data?.id == project?.data?.host?.id
                    )
                }
            }
        }

        AddMemberDialog(
            title = "Choose Member",
            showDialog = showAddMemberDialog,
            onDismiss = {
                showAddMemberDialog = false
            },
            onConfirm = {
                accessToken.value?.let { token ->
                    projectManagementViewModel.addMember(
                        id = id.toString(),
                        username = Username(it.username),
                        authorization = "Bearer $token"
                    )
                }
                isAdd = true

                showAddMemberDialog = false
            },
        )

        AlertDialog(
            title = "Something wrong",
            content = addMemberResponse?.message.toString(),
            showDialog = showAddAlertDialog,
            onDismiss = {
                showAddAlertDialog = false
            },
            onConfirm = {
                showAddAlertDialog = false
            })

        AlertDialog(
            title = "Something wrong",
            content = removeMemberResponse?.message.toString(),
            showDialog = showRemoveAlertDialog,
            onDismiss = {
                showRemoveAlertDialog = false
            },
            onConfirm = {
                showRemoveAlertDialog = false
            })

        AlertDialog(
            title = "Remove Member?",
            content = "Are you sure you want to remove this member?",
            showDialog = showDeleteDialog,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                accessToken.value?.let { token ->
                    projectManagementViewModel.removeMember(
                        id = id.toString(),
                        memberId = Id(memberId),
                        authorization = "Bearer $token"
                    )
                }
                isRemove = true
                showDeleteDialog = false
            }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        LaunchedEffect(project) {
            delay(500)
        }
    }
}


@Composable
fun TopBar(
    title: String,
    color: Color,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    onNavigateBack: () -> Unit = {}

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onNavigateBack() }) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow_icon),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        trailing?.invoke()
    }
}

@Composable
fun DescriptionSection(
    description: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
    ) {
        Text(
            text = "Description",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MembersSection(
    members: List<User> = emptyList(),
    onAddNewMember: () -> Unit = {},
    onDeleteMember: (String) -> Unit = {},
    onMemberProfile: (Int) -> Unit,
    hostId: Int = -1,
    isHost: Boolean
) {


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(horizontal = 36.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Members",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = { onAddNewMember() },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
            ) {
                Text(text = "+ Add", fontSize = 16.sp)
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.1f))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(members.size) { index ->
                    MemberItem(
                        MemberItem(
                            username = members[index].username ?: "Unknown",
                            backgroundColor = Color.Transparent,
                            avatar = R.drawable.avatar,
                        ),
                        onDelete = {
                            onDeleteMember(members[index].id.toString())
                        },
                        onClick = { onMemberProfile(members[index].id) },
                        modifier = Modifier.fillMaxWidth(0.7f),
                        hostId = hostId,
                        userId = members[index].id,
                        isHost = isHost
                    )
                }
            }
        }
    }
}