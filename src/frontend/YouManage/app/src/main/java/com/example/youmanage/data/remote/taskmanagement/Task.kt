package com.example.youmanage.data.remote.taskmanagement

import com.example.youmanage.data.remote.projectmanagement.User
import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("actual_end_date")
    val actualEndDate: Any,
    @SerializedName("actual_start_date")
    val actualStartDate: Any,
    val assignee: User,
    @SerializedName("create_at")
    val createdAt: String,
    val description: Any,
    @SerializedName("end_date")
    val endDate: String,
    val id: Int,
    val project: Int,
    @SerializedName("start_date")
    val startDate: String,
    val status: String,
    val title: String,
    @SerializedName("updated_at")
    val updatedAt: String
)