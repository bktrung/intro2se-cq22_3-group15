package com.example.youmanage.utils

import androidx.datastore.preferences.core.stringPreferencesKey
import org.json.JSONObject

object Constants {

    const val BASE_URL = "https://youmanage.c0smic.tech/"
    const val WEB_SOCKET = "https://youmanage.c0smic.tech/ws/"

    const val WEB_CLIENT_ID = "564163999961-mdjogd7b76hkc9fksc670ffn5702q0rg.apps.googleusercontent.com"
//    const val WEB_CLIENT_ID =
//        "76922283431-11olbqs5uu5fmq37m33svsrlam19pnt5.apps.googleusercontent.com"
    val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")


    val priorityChoice = listOf(
        "Low",
        "Medium",
        "High"
    )

    val statusMapping = listOf(
        "Pending" to "PENDING",
        "In Progress" to "IN_PROGRESS",
        "Completed" to "COMPLETED"
    )

}