package com.example.youmanage.data.remote.notification

import com.google.gson.annotations.SerializedName

data class NotificationSocket(
    val type: String? = null,
    val title: String? = null,
    val body: String? = null,
    @SerializedName("object")
    val objectContent: Object? = null

)
