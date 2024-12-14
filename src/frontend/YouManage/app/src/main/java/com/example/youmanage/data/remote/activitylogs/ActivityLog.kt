package com.example.youmanage.data.remote.activitylogs

data class ActivityLog(
    val type: String,
    val modelType: String,
    val objectData: Map<String, Any>,
    val timestamp: Long
)
