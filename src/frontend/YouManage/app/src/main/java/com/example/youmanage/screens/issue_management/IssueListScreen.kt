package com.example.youmanage.screens.issue_management

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.issusemanagement.Issue
import com.example.youmanage.screens.task_management.ButtonSection
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Constants.statusMapping
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.randomAvatar
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.TraceInProjectViewModel
import com.example.youmanage.viewmodel.issuemanagement.IssuesListViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Composable
fun IssueListScreen(
    onNavigateBack: () -> Unit = {},
    projectId: String,
    onCreateIssue: () -> Unit = {},
    onIssueDetail: (Int) -> Unit,
    onDisableAction: () -> Unit = {},
    issuesListViewModel: IssuesListViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    traceInProjectViewModel: TraceInProjectViewModel = hiltViewModel()
) {
    val backgroundColor = MaterialTheme.colorScheme.background

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val issueSocket by issuesListViewModel.issueSocket.observeAsState()
    val issues by issuesListViewModel.issues.observeAsState()

    val shouldDisableAction by traceInProjectViewModel.observeCombinedLiveData(projectId)
        .observeAsState(false)

    val webSocketUrl = "${WEB_SOCKET}project/$projectId/"
    var filterIssues by remember { mutableStateOf(emptyList<Issue>()) }

    // Fetch issues when the token is available
    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val bearerToken = "Bearer $token"

            supervisorScope {
                // Collecting the launched jobs
                val jobs = listOf(
                    launch {
                        try {
                            issuesListViewModel.getIssues(
                                projectId = projectId,
                                authorization = bearerToken
                            )
                        } catch (e: Exception) {
                            Log.e("IssueManagement", "Error fetching issues: ${e.message}")
                        }
                    }
                )
                // Waiting for all jobs to finish
                jobs.joinAll()
            }
        }
    }

    LaunchedEffect(shouldDisableAction){
        if(shouldDisableAction){
            onDisableAction()
        }
    }

    LaunchedEffect(Unit) {
        supervisorScope {
            launch {
                issuesListViewModel.connectToIssueWebSocket(webSocketUrl)
            }.join()
        }

    }

    LaunchedEffect(issueSocket) {
        supervisorScope {
            launch {
                issuesListViewModel.getIssues(
                    projectId = projectId,
                    authorization = "Bearer ${accessToken.value}"
                )
            }
        }
    }

    var isSelectedButton by rememberSaveable { mutableIntStateOf(0) }

    // Filter issues based on status selection
    LaunchedEffect(key1 = isSelectedButton, key2 = issues) {
        if (issues is Resource.Success) {
            filterIssues = issues?.data?.filter {
                it.status == statusMapping[isSelectedButton].second
            } ?: emptyList()
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
            com.example.youmanage.screens.project_management.TopBar(
                title = "Issue List",
                color = Color.Transparent,
                trailing = {
                    Spacer(modifier = Modifier.size(24.dp))
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
                    onClick = { onCreateIssue() },
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
                        "Create Issue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
        ) {
            Column {
                ButtonSection(
                    isSelectedButton = isSelectedButton,
                    onClick = { isSelectedButton = it },
                    status = statusMapping
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(550.dp)
                    ) {
                        items(filterIssues.size) { index ->
                            IssueItem(
                                title = filterIssues[index].title ?: "Unknown",
                                reporter = filterIssues[index].reporter?.username ?: "Unassigned",
                                reporterId = filterIssues[index].reporter?.id ?: -1,
                                onIssueClick = { onIssueDetail(filterIssues[index].id ?: -1) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IssueItem(
    title: String,
    reporter: String,
    reporterId: Int = -1,
    onIssueClick: () -> Unit = {}
) {
    Card(
        onClick = { onIssueClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Reporter Section
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = randomAvatar(reporterId)),
                    contentDescription = "Reporter Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Reporter",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = reporter,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun TopBar(
    title: String,
    onNavigateBack: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp)
            .padding(top = 24.dp),
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

        Spacer(modifier = Modifier.size(30.dp))
    }
}
