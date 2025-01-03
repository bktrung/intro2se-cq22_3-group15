package com.example.youmanage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.websocket.MemberObject
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.AuthenticationRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TraceInProjectViewModel @Inject constructor(
    private val webSocketRepo: WebSocketRepository,
    private val authRepo: AuthenticationRepository
) : ViewModel() {

    private val _projectSocketLiveData = MutableLiveData<Resource<WebSocketResponse<Project>>>()
    val projectSocketLiveData: LiveData<Resource<WebSocketResponse<Project>>>
        get() = _projectSocketLiveData

    private val _memberSocketLiveData = MutableLiveData<Resource<WebSocketResponse<MemberObject>>>()
    val memberSocketLiveData: LiveData<Resource<WebSocketResponse<MemberObject>>>
        get() = _memberSocketLiveData

    private val _userLiveData = MutableLiveData<Resource<User>?>()
    val userLiveData: LiveData<Resource<User>?>
        get() = _userLiveData

    private val _shouldDisableActionLiveData = MutableLiveData<Boolean>()
    val shouldDisableActionLiveData: LiveData<Boolean>
        get() = _shouldDisableActionLiveData

    private val _combinedLiveData = MediatorLiveData<Boolean>()

    private var isSourcesAdded = false

    // Chức năng kết hợp dữ liệu và theo dõi sự thay đổi
    fun observeCombinedLiveData(projectId: String): LiveData<Boolean> {
        _combinedLiveData.apply {
            // Dùng addSource để theo dõi sự thay đổi của từng LiveData
            if (!isSourcesAdded) {
                addSource(_projectSocketLiveData) { projectSocket ->
                    // Kiểm tra điều kiện dựa trên dữ liệu của tất cả các nguồn
                    value = checkConditions(
                        projectSocket,
                        _memberSocketLiveData.value,
                        projectId,
                        _userLiveData.value
                    )
                }
                addSource(_memberSocketLiveData) { memberSocket ->
                    value = checkConditions(
                        _projectSocketLiveData.value,
                        memberSocket,
                        projectId,
                        _userLiveData.value
                    )
                }
                addSource(_userLiveData) { user ->
                    value = checkConditions(
                        _projectSocketLiveData.value,
                        _memberSocketLiveData.value,
                        projectId,
                        user
                    )
                }
            }
            isSourcesAdded = true
        }
        return _combinedLiveData
    }

    // Kết nối WebSocket cho dự án và thành viên, đồng thời lấy thông tin người dùng
    fun connectToWebSocketAndUser(accessToken: String, url: String) {
        viewModelScope.launch {
            // Kết nối WebSocket cho dự án
            launch {
                webSocketRepo.connectToSocket(
                    url,
                    object : TypeToken<WebSocketResponse<Project>>() {},
                    _projectSocketLiveData
                )
            }

            // Kết nối WebSocket cho thành viên
            launch {
                webSocketRepo.connectToSocket(
                    url,
                    object : TypeToken<WebSocketResponse<MemberObject>>() {},
                    _memberSocketLiveData
                )
            }

            // Lấy thông tin người dùng
            launch {
                _userLiveData.postValue(authRepo.getUser(authorization = "Bearer $accessToken"))
            }
        }
    }

    // Kiểm tra các điều kiện có phải "disable action" không
    private fun checkConditions(
        projectSocket: Resource<WebSocketResponse<Project>>?,
        memberSocket: Resource<WebSocketResponse<MemberObject>>?,
        projectId: String,
        user: Resource<User>?
    ): Boolean {
        return when {
            // Kiểm tra điều kiện dự án bị xóa
            projectSocket is Resource.Success &&
                    projectSocket.data?.type == "project_deleted" &&
                    projectSocket.data.content?.id.toString() == projectId -> {
                true
            }
            // Kiểm tra điều kiện thành viên bị xóa
            memberSocket is Resource.Success &&
                    memberSocket.data?.type == "member_removed" &&
                    user is Resource.Success &&
                    memberSocket.data.content?.affectedMembers?.contains(user.data) == true -> {
                true
            }
            else -> false
        }
    }

    // Cập nhật dữ liệu WebSocket cho dự án
    fun updateProjectSocket(resource: Resource<WebSocketResponse<Project>>) {
        _projectSocketLiveData.postValue(resource)
    }

    // Cập nhật dữ liệu WebSocket cho thành viên
    fun updateMemberSocket(resource: Resource<WebSocketResponse<MemberObject>>) {
        _memberSocketLiveData.postValue(resource)
    }
}
