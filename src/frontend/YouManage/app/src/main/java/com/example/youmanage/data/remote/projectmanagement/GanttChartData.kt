package com.example.youmanage.data.remote.projectmanagement

import com.google.gson.annotations.SerializedName

data class GanttChartData(
    val id: Int,
    val title: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String
)
