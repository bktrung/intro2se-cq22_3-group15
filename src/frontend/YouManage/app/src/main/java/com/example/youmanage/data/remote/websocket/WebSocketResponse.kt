package com.example.youmanage.data.remote.websocket

import com.google.gson.annotations.SerializedName

data class WebSocketResponse<T>(
    val type: String? = null,
    @SerializedName("model_type")
    var modelType: String? = null,
    @SerializedName("object")
    val content: T? = null
)
