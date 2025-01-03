package com.example.youmanage.data.remote.issusemanagement

import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Task

data class Issue(
    val id: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val status: String? = null,
    val projectId: Int? = null,
    val reporter: User? = null,
    val assignee: User? = null,
    val task: Task? = null
)
