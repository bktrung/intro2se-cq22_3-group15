package com.example.youmanage.data.remote.activitylogs

data class Activity(
    val id: String,
    val userId: String,
    val action: String,
    val timestamp: Long,
    val modelType: String,
    val details: String
)
