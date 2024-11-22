package com.example.youmanage.data.remote.taskmanagement

import com.google.gson.annotations.SerializedName

data class TaskUpdate(
    val description: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("start_date")
    val startDate: String,
    val title: String
)