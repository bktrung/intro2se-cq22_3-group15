package com.example.youmanage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.taskmanagement.Comment
import com.example.youmanage.data.remote.taskmanagement.Content
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.data.remote.taskmanagement.TaskUpdateStatus
import com.example.youmanage.repository.TaskManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    private val repository: TaskManagementRepository
) : ViewModel() {

    private val _tasks = MutableLiveData<Resource<List<Task>>>()
    val tasks: LiveData<Resource<List<Task>>> = _tasks

    private val _task = MutableLiveData<Resource<Task>>()
    val task: MutableLiveData<Resource<Task>>
        get() = _task

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

    fun getTasks(
        projectId: String,
        authorization: String
    ){
        viewModelScope.launch {
            _tasks.value = repository.getTasks(projectId, authorization)
        }
    }

    fun createTask(
        projectId: String,
        task: TaskCreate,
        authorization: String
    ) {
        viewModelScope.launch {
            _task.value = repository.createTask(projectId, task, authorization)
        }
    }

    fun getTask(
        projectId: String,
        taskId: String,
        authorization: String
    ){
        viewModelScope.launch {
            _task.value = repository.getTask(projectId, taskId, authorization)
        }
    }

    fun updateTask(
        projectId: String,
        taskId: String,
        task: TaskUpdate,
        authorization: String
    ){
        viewModelScope.launch {
            _task.value = repository.updateTask(projectId, taskId, task, authorization)
        }
    }

    fun updateTaskStatusAndAssignee(
        projectId: String,
        taskId: String,
        task: TaskUpdateStatus,
        authorization: String
    ){
        viewModelScope.launch {
            _task.value = repository.updateTaskStatusAndAssignee(projectId, taskId, task, authorization)
        }
    }

    fun deleteTask(
        projectId: String,
        taskId: String,
        authorization: String
    ){
        viewModelScope.launch {
            _response.value = repository.deleteTask(projectId, taskId, authorization)
        }
    }

    fun getComments(
        projectId: String,
        taskId: String,
        authorization: String
    ){
        viewModelScope.launch {
            _comments.value = repository.getComments(projectId, taskId, authorization)
        }

    }

    fun postComment(
        projectId: String,
        taskId: String,
        comment: Content,
        authorization: String
    ){
        viewModelScope.launch {
            _comment.value = repository.postComment(projectId, taskId, comment, authorization)
        }
    }


    fun updateComment(
        projectId: String,
        taskId: String,
        commentId: String,
        comment: Content,
        authorization: String
    ){
        viewModelScope.launch {
            _comment.value = repository.updateComment(projectId, taskId, commentId, comment, authorization)
        }
    }

    fun deleteComment(
        projectId: String,
        taskId: String,
        commentId: String,
        authorization: String
    ){
        viewModelScope.launch {
            repository.deleteComment(projectId, taskId, commentId, authorization)
        }
    }

}