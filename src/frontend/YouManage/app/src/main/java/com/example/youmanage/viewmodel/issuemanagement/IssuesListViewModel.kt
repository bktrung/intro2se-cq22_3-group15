package com.example.youmanage.viewmodel.issuemanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.youmanage.data.remote.issusemanagement.Issue
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.IssuesRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class IssuesListViewModel @Inject constructor(
    private val issuesRepository: IssuesRepository,
    private val webSocketRepository: WebSocketRepository
): ViewModel() {

    private val _issues = MutableLiveData<Resource<List<Issue>>>()
    val issues: LiveData<Resource<List<Issue>>> = _issues

    private val _issueSocket = MutableLiveData<Resource<WebSocketResponse<Issue>>>()
    val issueSocket: LiveData<Resource<WebSocketResponse<Issue>>> = _issueSocket

    fun getIssues(
        projectId: String,
        authorization: String
    ) {
        viewModelScope.launch {
            _issues.value = withContext(Dispatchers.IO){
                issuesRepository.getIssues(projectId, authorization)
            }
        }
    }

    fun connectToIssueWebSocket(url: String) {
        viewModelScope.launch {
            _issueSocket.value = withContext(Dispatchers.IO) {
                webSocketRepository.connectToSocket(
                    url = url,
                    object : TypeToken<WebSocketResponse<Issue>>() {},
                    _issueSocket
                )
            }
        }
    }

}