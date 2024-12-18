package com.example.youmanage.data.remote.taskmanagement

import com.google.gson.annotations.SerializedName

data class TaskCreate(
    @SerializedName("assignee_id")
    val assigneeId: Int,
    @SerializedName("end_date")
    val endDate: String,
    val description: String? = null,
    @SerializedName("start_date")
    val startDate: String,
    val title: String,
    val priority: String? = null

)