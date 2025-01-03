package com.example.youmanage.data.remote.taskmanagement

import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Constructor

data class TaskWebSocket(
    val task: Task,
    val type: String,
)