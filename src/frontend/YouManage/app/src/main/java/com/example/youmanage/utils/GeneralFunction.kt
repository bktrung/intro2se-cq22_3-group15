package com.example.youmanage.utils

import org.json.JSONObject

fun extractMessages(jsonResponse: String): String {
    val messages = mutableListOf<String>()
    val jsonObject = JSONObject(jsonResponse)

    for (key in jsonObject.keys()) {
        val messageArray = jsonObject.getJSONArray(key)
        for (i in 0 until messageArray.length()) {
            messages.add(messageArray.getString(i))
        }
    }
    return messages.joinToString(separator = "\n")
}

