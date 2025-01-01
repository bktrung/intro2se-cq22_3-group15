package com.example.youmanage.utils

import android.os.Build
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.websocket.MemberObject
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import org.json.JSONObject
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.absoluteValue
import kotlin.random.Random

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

fun randomColor(index: Int): Int {
    val colors = listOf(
        Color(0xFFDAB00A),
        Color(0xffb6f36a),
        Color(0xff94dafb),
        Color(0xfffe9b64),
        Color(0xffc9a0ff),
        Color.Cyan.copy(alpha = 0.6f),
        Color(0xFFF44336).copy(alpha = 0.6f)
    )

    return colors[index % colors.size].hashCode()
}

fun randomAvatar(index: Int): Int {
    val avatars = listOf(
        R.drawable.avatar_01,
        R.drawable.avatar_02,
        R.drawable.avatar_03,
        R.drawable.avatar_04,
        R.drawable.avatar_05
    )
    if(index == -1) return R.drawable.no_avatar

    return avatars[index % avatars.size]
}

fun randomVibrantLightColor(): Color {
    var red: Int
    var green: Int
    var blue: Int

    do {
        red = Random.nextInt(128, 256)
        green = Random.nextInt(128, 256)
        blue = Random.nextInt(128, 256)
    } while ((red - green).absoluteValue < 50 && (green - blue).absoluteValue < 50 && (blue - red).absoluteValue < 50)

    return Color(red, green, blue)
}

// Process Access Token
fun decodeJWT(token: String): JSONObject? {
    return try {
        val payload = token.split(".")[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val decodedPayload = String(decodedBytes)
        JSONObject(decodedPayload)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun isTokenExpired(token: String): Boolean {
    val payload = decodeJWT(token) ?: return true
    val exp = payload.optLong("exp", 0L)
    Log.d("Token Exp", payload.toString())
    Log.d("Token Exp", exp.toString())
    val currentTime = System.currentTimeMillis() / 1000
    Log.d("Current Time", currentTime.toString())
    return currentTime > exp
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatToRelativeTime(isoString: String): String {
    val parsedTime = Instant.parse(isoString)
    val now = Instant.now()
    val seconds = ChronoUnit.SECONDS.between(parsedTime, now)
    val minutes = ChronoUnit.MINUTES.between(parsedTime, now)
    val hours = ChronoUnit.HOURS.between(parsedTime, now)
    val days = ChronoUnit.DAYS.between(parsedTime, now)

    return when {
        seconds <= 1L -> "Just now"
        seconds < 60 -> "$seconds seconds ago"
        minutes == 1L -> "1 minute ago"
        minutes < 60 -> "$minutes minutes ago"
        hours == 1L -> "1 hour ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "Yesterday"
        else -> "$days days ago"
    }
}

@Composable
fun HandleOutProjectWebSocket(
    memberSocket: Resource<WebSocketResponse<MemberObject>>?,
    projectSocket: Resource<WebSocketResponse<Project>>?,
    user: Resource<User>?,
    projectId: String,
    onDisableAction: () -> Unit
) {
    LaunchedEffect(
        key1 = memberSocket,
        key2 = projectSocket
    ) {
        // Kiểm tra thông báo từ WebSocket của dự án
        if (projectSocket is Resource.Success &&
            projectSocket.data?.type == "project_deleted" &&
            projectSocket.data.content?.id.toString() == projectId
        ) {
            onDisableAction()
        }

        // Kiểm tra thông báo từ WebSocket của thành viên
        if (memberSocket is Resource.Success &&
            memberSocket.data?.type == "member_removed" &&
            user is Resource.Success &&
            memberSocket.data.content?.affectedMembers?.contains(user.data) == true
        ) {
            onDisableAction()
        }
    }
}
