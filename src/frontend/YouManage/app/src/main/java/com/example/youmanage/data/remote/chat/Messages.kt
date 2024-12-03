package com.example.youmanage.data.remote.chat

data class Messages(
    val next: Any,
    val previous: Any,
    val results: List<Message>
)