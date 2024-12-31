package com.example.youmanage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.AuthenticationRepository
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.repository.TaskManagementRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    private val repository: TaskManagementRepository,
    private val projectManagementRepository: ProjectManagementRepository,
    private val webSocketRepository: WebSocketRepository
) : ViewModel() {

    // Tạo SupervisorJob cho toàn bộ ViewModel
    private val supervisorJob = SupervisorJob()

    // Tạo scope với SupervisorJob
    private val scope = CoroutineScope(Dispatchers.Main + supervisorJob)

    private val _tasks = MutableLiveData<Resource<List<Task>>>()
    val tasks: LiveData<Resource<List<Task>>> = _tasks

    private val _task = MutableLiveData<Resource<Task>>()
    val task: MutableLiveData<Resource<Task>>
        get() = _task

    private val _taskUpdate = MutableLiveData<Resource<Task>>()
    val taskUpdate: MutableLiveData<Resource<Task>>
        get() = _taskUpdate

    private val _response = MutableLiveData<Resource<String>>()
    val response: LiveData<Resource<String>>
        get() = _response

    private val _comments = MutableLiveData<Resource<List<Comment>>>()
    val comments: LiveData<Resource<List<Comment>>> get() = _comments

    private val _comment = MutableLiveData<Resource<Comment>>()
    val comment: LiveData<Resource<Comment>>
        get() = _comment

    private val _deleteCommentResponse = MutableLiveData<Resource<String>>()
    val deleteCommentResponse: LiveData<Resource<String>>
        get() = _deleteCommentResponse

    private val _taskSocket = MutableLiveData<Resource<WebSocketResponse<Task>>>()
    val taskSocket: LiveData<Resource<WebSocketResponse<Task>>>
        get() = _taskSocket

    private val _members = MutableLiveData<List<User>>(emptyList())
    val members: LiveData<List<User>>
        get() = _members

    private val _myTask = MutableLiveData<List<Task>>(emptyList())
    val myTask: LiveData<List<Task>>
        get() = _myTask

    private val _isHost = MutableLiveData<Boolean>(false)
    val isHost: LiveData<Boolean>
        get() = _isHost

    // Các phương thức như trước, sử dụng scope.launch thay vì viewModelScope.launch

    fun isHost(projectId: String, authorization: String) {
        scope.launch {
            val response = projectManagementRepository.isHost(projectId, authorization)
            if (response is Resource.Success) {
                _isHost.value = response.data?.isHost ?: false
            } else {
                _isHost.value = false
            }
        }
    }

    fun getMembers(projectId: String, authorization: String) {
        scope.launch {
            val response = projectManagementRepository.getMembers(projectId, authorization)
            if (response is Resource.Success) {
                _members.value = response.data ?: emptyList()
                _members.value = members.value?.plus(User(id = -1, username = "Unassigned"))
            } else {
                _members.value = emptyList()
            }
        }
    }

    fun getTasks(
        projectId: String,
        authorization: String
    ) {
        scope.launch {
            _tasks.value = repository.getTasks(projectId, authorization)
        }
    }

    fun getMyTask(authorization: String) {
        scope.launch {
            val response = repository.getMyTask(authorization)
            if (response is Resource.Success) {
                _myTask.value = response.data ?: emptyList()
            } else {
                _myTask.value = emptyList()
            }
        }
    }

    fun createTask(
        projectId: String,
        task: TaskCreate,
        authorization: String
    ) {
        scope.launch {
            _task.value = repository.createTask(projectId, task, authorization)
        }
    }

    fun getTask(
        projectId: String,
        taskId: String,
        authorization: String
    ) {
        scope.launch {
            _task.value = repository.getTask(projectId, taskId, authorization)
        }
    }

    fun updateTask(
        projectId: String,
        taskId: String,
        task: TaskUpdate,
        authorization: String
    ) {
        scope.launch {
            _taskUpdate.value = repository.updateTask(projectId, taskId, task, authorization)
        }
    }

    fun updateTaskStatusAndAssignee(
        projectId: String,
        taskId: String,
        task: TaskUpdateStatus,
        authorization: String
    ) {
        scope.launch {
            _taskUpdate.value = repository.updateTaskStatusAndAssignee(projectId, taskId, task, authorization)
        }
    }

    fun deleteTask(
        projectId: String,
        taskId: String,
        authorization: String
    ) {
        scope.launch {
            _response.value = repository.deleteTask(projectId, taskId, authorization)
        }
    }

    fun getComments(
        projectId: String,
        taskId: String,
        authorization: String
    ) {
        scope.launch {
            _comments.value = repository.getComments(projectId, taskId, authorization)
        }
    }

    fun postComment(
        projectId: String,
        taskId: String,
        comment: Content,
        authorization: String
    ) {
        scope.launch {
            _comment.value = repository.postComment(projectId, taskId, comment, authorization)
        }
    }

    fun updateComment(
        projectId: String,
        taskId: String,
        commentId: String,
        comment: Content,
        authorization: String
    ) {
        scope.launch {
            _comment.value = repository.updateComment(projectId, taskId, commentId, comment, authorization)
        }
    }

    fun deleteComment(
        projectId: String,
        taskId: String,
        commentId: String,
        authorization: String
    ) {
        scope.launch {
            _deleteCommentResponse.value = repository.deleteComment(
                projectId,
                taskId,
                commentId,
                authorization
            )
        }
    }

    fun connectToTaskWebSocket(url: String) {
        scope.launch {
            webSocketRepository.connectToSocket(
                url,
                object : TypeToken<WebSocketResponse<Task>>() {},
                _taskSocket
            )
        }
    }

}
