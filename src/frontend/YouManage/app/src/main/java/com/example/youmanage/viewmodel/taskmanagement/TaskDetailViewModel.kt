package com.example.youmanage.viewmodel.taskmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
import com.example.youmanage.repository.ChangeRequestRepository
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.repository.TaskManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val taskManagementRepository: TaskManagementRepository,
    private val projectManagementRepository: ProjectManagementRepository,
    private val changeRequestRepository: ChangeRequestRepository
) : ViewModel() {


    private val _members = MutableLiveData<List<User>>(emptyList())
    val members: LiveData<List<User>> = _members

    private val _isHost = MutableLiveData<Boolean>()
    val isHost: LiveData<Boolean> = _isHost

    private val _comments = MutableLiveData<Resource<List<Comment>>>()
    val comments: LiveData<Resource<List<Comment>>> = _comments

    private val _task = MutableLiveData<Resource<Task>>()
    val task: LiveData<Resource<Task>> = _task

    private val _comment = MutableLiveData<Resource<Comment>>()
    val comment: LiveData<Resource<Comment>> = _comment

    private val _deleteCommentResponse = MutableLiveData<Resource<String>>()
    val deleteCommentResponse: LiveData<Resource<String>> = _deleteCommentResponse

    private val _response = MutableLiveData<Resource<String>>()
    val response: LiveData<Resource<String>> = _response

    private val _taskUpdate = MutableLiveData<Resource<Task>>()
    val taskUpdate: MutableLiveData<Resource<Task>>
        get() = _taskUpdate

    private val _requestResponse = MutableLiveData<Resource<ChangeRequest>>()
    val requestResponse: LiveData<Resource<ChangeRequest>> = _requestResponse

    fun getMembers(projectId: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.getMembers(projectId, authorization)
            }

            if (response is Resource.Success) {
                _members.value = response.data ?: emptyList()
            }
        }
    }

    fun getTask(projectId: String, taskId: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                taskManagementRepository.getTask(projectId, taskId, authorization)
            }

            if (response is Resource.Success) {
                _task.value = response
            }

        }
    }

    fun getComments(projectId: String, taskId: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                taskManagementRepository.getComments(projectId, taskId, authorization)
            }
            if (response is Resource.Success) {
                _comments.value = response
            }
        }
    }

    fun postComment(
        projectId: String,
        taskId: String,
        comment: Content,
        authorization: String
    ) {
        viewModelScope.launch {
            _comment.value = withContext(Dispatchers.IO) {
                taskManagementRepository.postComment(projectId, taskId, comment, authorization)
            }
        }
    }

    fun updateComment(
        projectId: String,
        taskId: String,
        commentId: String,
        comment: Content,
        authorization: String
    ) {
        viewModelScope.launch {
            _comment.value = withContext(Dispatchers.IO) {
                taskManagementRepository.updateComment(
                    projectId,
                    taskId,
                    commentId,
                    comment,
                    authorization
                )
            }
        }
    }

    fun deleteComment(
        projectId: String,
        taskId: String,
        commentId: String,
        authorization: String
    ) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                taskManagementRepository.deleteComment(
                    projectId,
                    taskId,
                    commentId,
                    authorization
                )
            }
            _deleteCommentResponse.value = response
        }
    }

    fun updateTask(
        projectId: String,
        taskId: String,
        task: TaskUpdate,
        authorization: String
    ) {
        viewModelScope.launch {
            _taskUpdate.value = withContext(Dispatchers.IO){
                taskManagementRepository.updateTask(projectId, taskId, task, authorization)
            }
        }
    }

    fun updateTaskStatusAndAssignee(
        projectId: String,
        taskId: String,
        task: TaskUpdateStatus,
        authorization: String
    ) {
        viewModelScope.launch {
            _taskUpdate.value = withContext(Dispatchers.IO){
                taskManagementRepository.updateTaskStatusAndAssignee(projectId, taskId, task, authorization)
            }
        }
    }


    fun deleteTask(
        projectId: String,
        taskId: String,
        authorization: String
    ) {
        viewModelScope.launch {
            _response.value = withContext(Dispatchers.IO){
                taskManagementRepository.deleteTask(projectId, taskId, authorization)
            }
        }
    }

    fun isHost(projectId: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.isHost(projectId, authorization)
            }
            if (response is Resource.Success) {
                _isHost.value = response.data?.isHost ?: false
            }
        }
    }

    fun createChangeRequest(
        projectId: Int,
        changeRequest: SendChangeRequest,
        authorization: String
    ) {
        viewModelScope.launch {
            _requestResponse.value = withContext(Dispatchers.IO) {
                changeRequestRepository.createChangeRequest(
                    projectId,
                    changeRequest,
                    authorization
                )
            }
        }
    }

    fun loadTaskDetails(projectId: String, taskId: String, authorization: String) {
        viewModelScope.launch {
            supervisorScope {
                val getTaskJob = launch {
                    getTask(projectId, taskId, authorization)
                }
                val getCommentsJob = launch {
                    getComments(projectId, taskId, authorization)
                }
                val getMembersJob = launch {
                    getMembers(projectId, authorization)
                }

                val isHostJob = launch {
                    isHost(projectId, authorization)
                }
                // Đợi tất cả các coroutine hoàn thành (không bắt buộc)
                getTaskJob.join()
                getCommentsJob.join()
                getMembersJob.join()
                isHostJob.join()

            }
        }
    }


}