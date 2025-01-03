package com.example.youmanage.viewmodel.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.repository.TaskManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MyTaskViewModel @Inject constructor(
    private val taskManagementRepository: TaskManagementRepository
) : ViewModel(){

    private val _myTasks = MutableLiveData<List<Task>>(emptyList())
    val myTasks: LiveData<List<Task>> get() = _myTasks

    fun getMyTasks(token: String){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                taskManagementRepository.getMyTask("Bearer $token")
            }

            _myTasks.value =  if(response is Resource.Success){
                response.data ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

}