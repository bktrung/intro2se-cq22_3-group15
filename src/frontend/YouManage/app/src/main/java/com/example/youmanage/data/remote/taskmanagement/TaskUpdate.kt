package com.example.youmanage.data.remote.taskmanagement

import com.google.gson.annotations.SerializedName

data class TaskUpdate(
    val title: String = "",
    val description: String = "",
    @SerializedName("start_date")
    val startDate: String = "",
    @SerializedName("end_date")
    val endDate: String = "",
    val priority: String? = null
)