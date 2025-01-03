package com.example.youmanage.data.remote.projectmanagement

import com.google.gson.annotations.SerializedName

data class Progress(
    @SerializedName("pending_tasks")
    val pending: Int? = null,
    @SerializedName("in_progress_tasks")
    val inProgress: Int? = null,
    @SerializedName("completed_tasks")
    val completed: Int? = null,
    @SerializedName("total_tasks")
    val total: Int = 0
)

