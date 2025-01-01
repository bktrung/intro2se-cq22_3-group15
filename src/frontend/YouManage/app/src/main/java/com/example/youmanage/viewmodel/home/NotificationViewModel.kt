package com.example.youmanage.viewmodel.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.notification.Notification
import com.example.youmanage.data.remote.notification.NotificationSocket
import com.example.youmanage.repository.NotificationRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val supervisorJob = SupervisorJob() // Tạo SupervisorJob
    private val scope = CoroutineScope(Dispatchers.Main + supervisorJob) // Tạo CoroutineScope với SupervisorJob

    private val _notificationFromSocket = MutableLiveData<Resource<NotificationSocket>>()
    val notificationFromSocket: LiveData<Resource<NotificationSocket>> = _notificationFromSocket

    private val _notifications = MutableLiveData<List<Notification>>(emptyList())
    val notifications: LiveData<List<Notification>> = _notifications

    private val _message = MutableLiveData<Resource<Message>>()
    val message: LiveData<Resource<Message>> = _message

    private var nextCursor: String? = null
    private var preCursor: String? = null
    var isLoading = MutableStateFlow(false)

    companion object {
        private val _unreadCount = MutableStateFlow(0)  // Xác định rõ kiểu Int
        val unreadCount = _unreadCount.asStateFlow()
    }

    fun getUnreadCountFlow(): StateFlow<Int> = unreadCount

    private fun markAsRead(notificationId: Int) {
        _notifications.value = notifications.value?.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else notification
        }
    }

    private fun markAllAsRead() {
        _notifications.value = notifications.value?.map { notification ->
            notification.copy(isRead = true)
        }
    }

    private fun deleteNotification(notificationId: Int) {
        _notifications.value = _notifications.value?.filter { it.id != notificationId }
    }

    fun connectToWebSocket(url: String) {
        scope.launch {
            repository.connectToSocket(url, _notificationFromSocket)
        }
    }

    fun getNotifications(
        cursor: String? = null,
        authorization: String
    ){
        scope.launch {

            isLoading.value = true

            val response = repository.getNotifications(
                cursor = cursor,
                authorization = authorization
            )

            try {
                if(response is Resource.Success){
                    response.data?.let {
                        nextCursor = it.next?.substringAfter("cursor=")
                        Log.d("ChatViewModel", "getMessages: $nextCursor")
                        preCursor = it.previous
                        _notifications.value = _notifications.value?.plus(it.results)
                        Log.d("ChatViewModel", "getMessages: ${_notifications.value}")
                    }
                }
            } catch(e: Exception) {
                Log.e("ChatViewModel", "Exception: ${e.message}")
            } finally {
                delay(500)
                isLoading.value = false
            }
        }
    }

    fun getMoreActivityLogs(
        authorization: String
    ){
        nextCursor?.let {
            getNotifications(it, authorization)
        }
    }

    fun getUnreadCountNotifications(authorization: String) {
        scope.launch {
            val response = repository.getUnreadCountNotifications(authorization)

            if(response is Resource.Success){
                _unreadCount.value = response.data?.count ?: 0
            } else if(response is Resource.Error){
                _unreadCount.value = 0
            }
        }
    }

    fun markAsRead(
        notificationId: Int,
        authorization: String
    ){
        scope.launch {
            val response = repository.markAsRead(notificationId, authorization)
            if(response is Resource.Success){
                markAsRead(notificationId)
                _unreadCount.value = repository.getUnreadCountNotifications(authorization).data?.count ?: 0
            }
        }
    }

    fun readAll(
        authorization: String
    ){
        scope.launch {
            val response = repository.readAll(authorization)
            if(response is Resource.Success){
                markAllAsRead()
                _unreadCount.value = repository.getUnreadCountNotifications(authorization).data?.count ?: 0
            }
        }
    }

    fun deleteNotification(
        notificationId: Int,
        authorization: String
    ){
        scope.launch {
            val response = repository.deleteNotification(notificationId, authorization)
            if(response is Resource.Success){
                _message.value = Resource.Success(Message(response.data!!))
                deleteNotification(notificationId)
                _unreadCount.value = repository.getUnreadCountNotifications(authorization).data?.count ?: 0
            } else if(response is Resource.Error){
                _message.value = Resource.Error("Delete Failed!")
            }
        }
    }


}
