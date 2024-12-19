package com.example.youmanage.repository

import android.util.Log
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.activitylogs.Activities
import com.example.youmanage.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ActivityLogRepository @Inject constructor(
    private val api: ApiInterface
) {
    suspend fun getActivityLogs(
        projectId: String,
        page: Int? = null,
        authentication: String
    ): Resource<Activities> {
        return try {
            Log.d("ActivityLogRepository", "getActivities: ${api.getActivityLogs(
                projectId,
                page,
                authentication)}")

            Resource.Success(api.getActivityLogs(
                projectId,
                page,
                authentication))

        } catch (e: Exception) {
            Log.d("ActivityLogRepository", "getActivities: ${e.message}")
            Resource.Error(e.message ?: "Unknown error occurred")
        }
    }
}