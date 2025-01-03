package com.example.youmanage.data.remote.chat

data class Messages(
    val next: String? = null,
    val previous: String? = null,
    val results: List<Message>
)