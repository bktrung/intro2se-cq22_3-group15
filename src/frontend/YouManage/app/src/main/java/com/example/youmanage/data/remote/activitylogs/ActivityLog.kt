package com.example.youmanage.data.remote.activitylogs

data class Activities(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Activity>
)
