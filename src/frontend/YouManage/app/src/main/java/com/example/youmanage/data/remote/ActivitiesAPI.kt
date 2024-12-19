package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.activitylogs.Activities
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivitiesAPI {
    @GET("/projects/{projectId}/activities/")
    suspend fun getActivityLogs(
        @Path("projectId") projectId: String,
        @Query("page") page: Int? = null,
        @Header("Authorization") authorization: String
    ): Activities
}