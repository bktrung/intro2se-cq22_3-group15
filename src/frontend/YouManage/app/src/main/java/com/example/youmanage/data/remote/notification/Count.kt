package com.example.youmanage.data.remote.notification

import com.google.gson.annotations.SerializedName

data class Count(
    @SerializedName("unread_count")
    val count: Int? = null
)
