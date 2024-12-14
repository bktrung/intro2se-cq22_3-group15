package com.example.youmanage.repository

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.activitylogs.Activity
import com.example.youmanage.data.remote.activitylogs.ActivityLog
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@ActivityScoped
class ActivityLogRepository @Inject constructor(
    private val api: ApiInterface
) {
    suspend fun fetchActivityLogs(projectId: String, authorization: String): List<Activity> {
        return api.getActivityLogs(projectId, authorization)
    }
}