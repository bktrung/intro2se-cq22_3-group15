package com.example.youmanage.data.remote

import android.database.Cursor
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.notification.Count
import com.example.youmanage.data.remote.notification.DeviceTokenRequest
import com.example.youmanage.data.remote.notification.Notifications
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationAPI {

    @GET("/notifications/")
    suspend fun getNotifications(
        @Query("cursor") cursor: String? = null,
        @Header("Authorization") authorization: String
    ): Notifications

    @GET("/notifications/unread/count/")
    suspend fun getUnreadCountNotifications(
        @Header("Authorization") authorization: String
    ) : Count

    @POST("/notifications/{notification_id}/read/")
    suspend fun markAsRead(
        @Path("notification_id") notificationId: Int,
        @Header("Authorization") authorization: String
    ) : Message

    @POST("/notifications/read-all/")
    suspend fun readAll(
        @Header("Authorization") authorization: String
    ): Message

    @DELETE("/notifications/{notification_id}/")
    suspend fun deleteNotification(
        @Path("notification_id") notificationId: Int,
        @Header("Authorization") authorization: String
    ):Response<Unit>

    @POST("/device-token/")
    suspend fun sendDeviceToken(
        @Body token: DeviceTokenRequest
    ): Response<Unit>

    @POST("/device-token/user/assign/")
    suspend fun assignDeviceToken(
        @Body token: DeviceTokenRequest,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    @POST("/device-token/user/unassign/")
    suspend fun unassignDeviceToken(
        @Body token: DeviceTokenRequest,
        @Header("Authorization") authorization: String
    ): Response<Unit>

}