package com.example.youmanage.data.remote.issusemanagement

import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Task

data class Issue(
    val id: Int,
    val title: String,
    val description: String?,
    val status: String,
    val projectId: Int,
    val reporter: User,
    val assignee: User,
    val task: Task
)
