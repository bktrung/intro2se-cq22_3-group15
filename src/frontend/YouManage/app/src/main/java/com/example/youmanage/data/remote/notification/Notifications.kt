package com.example.youmanage.data.remote.notification

data class Notifications(
    val next: String? = null,
    val previous: String? = null,
    val results: List<Notification> = emptyList()
)