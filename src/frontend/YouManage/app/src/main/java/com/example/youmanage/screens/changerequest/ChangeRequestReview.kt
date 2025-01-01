package com.example.youmanage.screens.changerequest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.youmanage.screens.project_management.TopBar
import com.example.youmanage.screens.task_management.ButtonBottomBar

@Preview
@Composable
fun ChangeRequestReviewScreen(
    changeRequestId: String = "",
    projectId: String = "",
    onNavigateBack: () -> Unit = {},

) {

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),

        topBar = {
            TopBar(
                title = "Request Detail",
                color = Color.Transparent,
                trailing = {
                    Box(modifier = Modifier.size(10.dp))
                },
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            ButtonBottomBar(
                titleYes = "Approve",
                titleNo = "Reject",
                onSaveClick = {
                },
                onDeleteClick = {
                }
            )
        }

    ) {
        paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            Text(
                "Type",

            )

        }
    }





}