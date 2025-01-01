package com.example.youmanage.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.issusemanagement.Issue
import com.example.youmanage.data.remote.issusemanagement.IssueCreate
import com.example.youmanage.data.remote.issusemanagement.IssueUpdate
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.IssuesRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class IssuesViewModel @Inject constructor(
    private val repository: IssuesRepository,
    private val webSocketRepository: WebSocketRepository
) : ViewModel() {

    private val supervisorJob = SupervisorJob() // Tạo SupervisorJob
    private val scope = CoroutineScope(Dispatchers.Main + supervisorJob) // Tạo CoroutineScope với SupervisorJob

    private val _issues = MutableLiveData<Resource<List<Issue>>>()
    val issues: LiveData<Resource<List<Issue>>> = _issues

    private val _issue = MutableLiveData<Resource<Issue>>()
    val issue: LiveData<Resource<Issue>> get() = _issue

    private val _response = MutableLiveData<Resource<String>>()
    val response: LiveData<Resource<String>> get() = _response

    private val _issueUpdate = MutableLiveData<Resource<Issue>>()
    val issueUpdate: LiveData<Resource<Issue>> get() = _issueUpdate

    private val _issueSocket = MutableLiveData<Resource<WebSocketResponse<Issue>>>()
    val issueSocket: LiveData<Resource<WebSocketResponse<Issue>>>
        get() = _issueSocket


    fun getIssues(projectId: String, authorization: String) {
        viewModelScope.launch {
            _issues.value = repository.getIssues(projectId, authorization)
        }
    }

    // Cập nhật hàm createIssue sử dụng scope mới
    fun createIssue(
        projectId: String,
        issue: IssueCreate,
        authorization: String
    ) {
        scope.launch {
            _issue.value = repository.createIssue(projectId, issue, authorization)
        }
    }

    // Cập nhật hàm getIssue sử dụng scope mới
    fun getIssue(
        projectId: String,
        issueId: String,
        authorization: String
    ) {
        scope.launch {
            _issue.value = repository.getIssue(projectId, issueId, authorization)
        }
    }

    // Cập nhật hàm updateIssue sử dụng scope mới
    fun updateIssue(
        projectId: String,
        issueId: String,
        issueUpdate: IssueUpdate,
        authorization: String
    ) {
        scope.launch {
            _issueUpdate.value = repository.updateIssue(projectId, issueId, issueUpdate, authorization)
        }
    }

    // Cập nhật hàm deleteIssue sử dụng scope mới
    fun deleteIssue(
        projectId: String,
        issueId: String,
        authorization: String
    ) {
        scope.launch {
            _response.value = repository.deleteIssue(projectId, issueId, authorization)
        }
    }

    // Cập nhật hàm connectToIssueWebSocket sử dụng scope mới
    fun connectToIssueWebSocket(url: String) {
        scope.launch {
            webSocketRepository.connectToSocket(
                url,
                object : TypeToken<WebSocketResponse<Issue>>() {},
                _issueSocket
            )
        }
    }


}
