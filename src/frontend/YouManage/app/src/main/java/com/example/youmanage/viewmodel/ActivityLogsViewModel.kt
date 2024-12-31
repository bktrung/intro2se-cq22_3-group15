package com.example.youmanage.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.activitylogs.Activity
import com.example.youmanage.repository.ActivityLogRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class ActivityLogsViewModel @Inject constructor(
    private val repository: ActivityLogRepository
) : ViewModel() {

    // Tạo SupervisorJob để ngăn chặn việc hủy các coroutine khác
    private val supervisorJob = SupervisorJob()
    private val viewModelScopeWithSupervisor = CoroutineScope(Dispatchers.Main + supervisorJob)

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
        viewModelScopeWithSupervisor.launch {
            try {
                isLoading.value = true

                val response = repository.getActivityLogs(
                    projectId = projectId,
                    page = page,
                    authentication = authorization
                )

                if (response is Resource.Success) {
                    response.data?.let {
                        nextCursor = it.next?.substringAfter("page=")
                        Log.d("ChatViewModel", "getMessages: $nextCursor")
                        preCursor = it.previous
                        _activityLogs.value += it.results
                        Log.d("ChatViewModel", "getMessages: ${_activityLogs.value}")
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

// Giả lập dữ liệu mẫu
