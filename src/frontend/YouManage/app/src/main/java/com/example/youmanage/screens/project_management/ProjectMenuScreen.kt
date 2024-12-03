package com.example.youmanage.screens.project_management

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.youmanage.R


data class ProjectMenuItem(
    val title: String,
    val icon: Int,
    val color: Color,
    val onClick: () -> Unit = {}
)

@Preview
@Composable
fun ProjectMenuScreen(
    onNavigateBack: () -> Unit = {},
    onTaskList: () -> Unit = {},
    onChatRoom: () -> Unit = {}
) {

    val projectMenuItems = listOf(
        ProjectMenuItem(
            title = "Task List",
            icon = R.drawable.task_icon,
            color = Color.Black,
            onClick = { onTaskList() }
        ),
        ProjectMenuItem(
            title = "Member",
            icon = R.drawable.user_icon,
            color = Color.Black
        ),
        ProjectMenuItem(
            title = "Project Setting",
            icon = R.drawable.setting_icon,
            color = Color.Black
        ),
        ProjectMenuItem(
            title = "Chat Room",
            icon = R.drawable.bubble_chat,
            color = Color.Black,
            onClick = {
                onChatRoom()
            }
        ),
        ProjectMenuItem(
            title = "Delete Project",
            icon = R.drawable.trash_icon,
            color = Color.Black
        )
    )

    Scaffold(
        topBar = {
            TopBar(
                title = "Project Menu",
                trailing = {
                    Spacer(modifier = Modifier.size(24.dp))
                },
                color = Color.Transparent,
                onNavigateBack = { onNavigateBack() }
            )
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 42.dp)
            ) {

                projectMenuItems.forEachIndexed{
                    _, item ->
                    MenuItem(
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.title,
                                tint = item.color
                            )
                        },
                        title = item.title,
                        onClick = item.onClick
                    )
                }
            }

        }

    }

}

@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    title: String,
    trailingIcon: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0x0D000000))
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)

        ) {
            trailingIcon()

            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp

            )

            Spacer(modifier = modifier.size(24.dp))
        }
    }
}