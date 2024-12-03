package com.example.youmanage.data.remote.taskmanagement

import com.example.youmanage.data.remote.projectmanagement.User
import com.google.gson.annotations.SerializedName

data class Task(
    @SerializedName("actual_end_date")
    val actualEndDate: String? = null,
    @SerializedName("actual_start_date")
    val actualStartDate: String? = null,
    val assignee: User = User(),
    @SerializedName("create_at")
    val createdAt: String = "",
    val description: String? = null,
    @SerializedName("end_date")
    val endDate: String = "",
    val id: Int = 0,
    val issues: List<String> = emptyList(),
    val priority: Int? = null,
    val project: Int = 0,
    @SerializedName("start_date")
    val startDate: String = "",
    val status: String = "",
    val title: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = ""

)