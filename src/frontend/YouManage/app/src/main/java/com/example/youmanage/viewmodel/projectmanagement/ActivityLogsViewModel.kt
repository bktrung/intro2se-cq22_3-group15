package com.example.youmanage.viewmodel.projectmanagement

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.activitylogs.Activity
import com.example.youmanage.repository.ActivityLogRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ActivityLogsViewModel @Inject constructor(
    private val repository: ActivityLogRepository
) : ViewModel() {

    private val _activityLogs = MutableStateFlow<List<Activity>>(emptyList())
    val activityLogs: StateFlow<List<Activity>> = _activityLogs

    private var nextCursor: String? = null
    private var preCursor: String? = null
    var isLoading = MutableStateFlow(false)

    fun getActivityLogs(
        projectId: String,
        page: Int? = null,
        authorization: String
    ) {
        viewModelScope.launch {
            try {
                isLoading.value = true

                val response = withContext(Dispatchers.IO) {
                    repository.getActivityLogs(
                        projectId = projectId,
                        page = page,
                        authentication = authorization
                    )
                }

                if (response is Resource.Success) {
                    response.data?.let {
                        nextCursor = it.next?.substringAfter("page=")
                        preCursor = it.previous
                        _activityLogs.value += it.results

                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception: ${e.message}")
            } finally {
                delay(500)
                isLoading.value = false
            }
        }
    }

    fun getMoreActivityLogs(
        projectId: String,
        authorization: String
    ) {
        nextCursor?.let {
            getActivityLogs(projectId, it.toInt(), authorization)
        }
    }
}


