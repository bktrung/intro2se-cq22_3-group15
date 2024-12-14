package com.example.youmanage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.activitylogs.Activity
import com.example.youmanage.repository.ActivityLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityLogsViewModel @Inject constructor(
    private val repository: ActivityLogRepository
) : ViewModel() {

    private val _activityLogs = MutableStateFlow<List<Activity>>(emptyList())
    val activityLogs: StateFlow<List<Activity>> = _activityLogs

    fun loadActivityLogs(projectId: String, token: String) {
        viewModelScope.launch {
            try {
                //val logs = repository.fetchActivityLogs(projectId, "Bearer $token")
                val logs = getSampleActivityLogs()
                _activityLogs.value = logs
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
                _activityLogs.value = emptyList()
            }
        }
    }
}

// Giả lập dữ liệu mẫu
private fun getSampleActivityLogs(): List<Activity> {
    return listOf(
        Activity(
            id = "1",
            userId = "user123",
            action = "Added Task",
            timestamp = System.currentTimeMillis(),
            modelType = "Task",
            details = "Task X was added to the project"
        ),
        Activity(
            id = "2",
            userId = "user456",
            action = "Updated Task",
            timestamp = System.currentTimeMillis() - 86400000, // 1 ngày trước
            modelType = "Task",
            details = "Task Y was updated"
        ),
        Activity(
            id = "3",
            userId = "user789",
            action = "Completed Task",
            timestamp = System.currentTimeMillis() - 172800000, // 2 ngày trước
            modelType = "Task",
            details = "Task Z was completed"
        )
    )
}

