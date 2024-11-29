package com.example.youmanage.data.remote.chat

data class MessageResponse(
    val id: Int,
    val author: String,
    val content: String,
    val timestamp: String
)
