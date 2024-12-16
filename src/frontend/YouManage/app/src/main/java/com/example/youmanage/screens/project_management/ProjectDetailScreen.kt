package com.example.youmanage.screens.project_management

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Username
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.PieChart
import com.example.youmanage.screens.components.PieChartInput
import com.example.youmanage.screens.components.pieChartInput
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel

@Composable
fun ProjectDetailScreen(
    backgroundColor: Color = Color(0xffBAE5F5),
    id: Int,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onClickMenu: () -> Unit,
    onDisableAction: () -> Unit
) {
    val project by projectManagementViewModel.project.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val addMemberResponse by projectManagementViewModel.addMemberResponse.observeAsState()
    val removeMemberResponse by projectManagementViewModel.deleteMemberResponse.observeAsState()
    val projectProgress by projectManagementViewModel.progress.observeAsState()
    val memberSocket by projectManagementViewModel.memberSocket.observeAsState()
    val projectSocket by projectManagementViewModel.projectSocket.observeAsState()
    val user by authenticationViewModel.user.observeAsState()

    var pieChartInputList by remember { mutableStateOf<List<PieChartInput>>(emptyList()) }

    var showAddMemberDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveAlertDialog by rememberSaveable { mutableStateOf(false) }
    var showAddAlertDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var isRemove by remember { mutableStateOf(false) }
    var isAdd by remember { mutableStateOf(false) }

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
        Log.d("Again", "Again")
        if (addMemberResponse is Resource.Error && isAdd) {
            showAddAlertDialog = true
        }
    }

    LaunchedEffect(removeMemberResponse) {
        if (removeMemberResponse is Resource.Error && isRemove) {
            showRemoveAlertDialog = true
        }
    }

    LaunchedEffect(accessToken.value)
    {
        accessToken.value?.let {
            val webSocketUrl = "${WEB_SOCKET}project/$id/"
            authenticationViewModel.getUser("Bearer $it")
            projectManagementViewModel.connectToProjectWebsocket(url = webSocketUrl)
            projectManagementViewModel.connectToMemberWebsocket(url = webSocketUrl)
        }
    }

    LaunchedEffect(
        key1 = memberSocket,
        key2 = projectSocket
    ) {
        if (
            projectSocket is Resource.Success &&
            projectSocket?.data?.type == "project_deleted" &&
            projectSocket?.data?.content?.id.toString() == id.toString()
        ) {
            onDisableAction()
        }

        if (
            memberSocket is Resource.Success &&
            memberSocket?.data?.type == "member_removed" &&
            user is Resource.Success &&
            memberSocket?.data?.content?.affectedMembers?.
            contains(user?.data) == true
        ) {
            onDisableAction()
        }
    }

    LaunchedEffect(projectProgress) {
        if (projectProgress is Resource.Success) {

            val total = projectProgress?.data?.total ?: 1
            val pending = projectProgress?.data?.pending ?: 0
            val inProgress = projectProgress?.data?.inProgress ?: 0
            val completed = projectProgress?.data?.completed ?: 0

            pieChartInputList = listOf(
                PieChartInput(
                    color = Color(0xffbaf4ca),
                    value = pending.toDouble().div(total) * 100.0,
                    description = "Completed"
                ),
                PieChartInput(
                    color = Color(0xfffccdcd),
                    value = inProgress.toDouble().div(total) * 100.0,
                    description = "In Progress"
                ),
                PieChartInput(
                    color = Color(0xffedf0f2),
                    value = completed.toDouble().div(total) * 100.0,
                    description = "Pending"
                ),
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
                        IconButton(onClick = { onClickMenu() }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    },
                    color = Color(0xffBAE5F5),
                    onNavigateBack = { onNavigateBack() }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(top = 24.dp)

        ) { paddingValues ->

            val scrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(backgroundColor)

            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {

                    if (projectProgress is Resource.Success) {
                        PieChart(
                            input = pieChartInputList,
                            modifier = Modifier.padding(36.dp)
                        )
                    }

                    DescriptionSection(
                        description = project?.data?.description.toString()
                    )

                    MembersSection(
                        onAddNewMember = {
                            showAddMemberDialog = true
                        },

                        onDeleteMember = {
                            memberId = it
                            showDeleteDialog = true
                        },
                        members = project?.data?.members ?: emptyList()
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
                contentDescription = "Back"
            )
        }
        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
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
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = Color.Black.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MembersSection(
    members: List<User> = emptyList(),
    onAddNewMember: () -> Unit = {},
    onDeleteMember: (String) -> Unit = {}
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
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { onAddNewMember() },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                modifier = Modifier.border(
                    1.dp,
                    Color.Black,
                    RoundedCornerShape(30.dp)
                )
            ) {
                Text(text = "+ Add", fontSize = 16.sp, color = Color.Black)
            }

        }


        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(top = 10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            items(members.size) { index ->
                MemberItem(
                    MemberItem(
                        username = members[index].username,
                        backgroundColor = Color.Transparent,
                        avatar = R.drawable.avatar
                    ),
                    onDelete = {
                        onDeleteMember(members[index].id.toString())
                    },
                    modifier = Modifier.fillMaxWidth(0.7f)
                )
            }
        }
    }
}