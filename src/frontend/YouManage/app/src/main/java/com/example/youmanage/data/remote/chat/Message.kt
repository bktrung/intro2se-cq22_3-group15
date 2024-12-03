package com.example.youmanage.data.remote.chat

import com.example.youmanage.data.remote.projectmanagement.User

data class Message(
    val author: User,
    val content: String,
    val id: Int,
    val project: Int,
    val timestamp: String
)