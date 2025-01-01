package com.example.youmanage.viewmodel.projectmanagement

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.projectmanagement.GanttChartData
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GanttChartViewModel @Inject constructor(
    private val projectRepo: ProjectManagementRepository
): ViewModel() {

    private val _ganttChartData = MutableLiveData<Resource<List<GanttChartData>>>()
    val ganttChartData: MutableLiveData<Resource<List<GanttChartData>>> = _ganttChartData

    private val _dueDate = MutableLiveData<String>()
    val dueDate: MutableLiveData<String> = _dueDate

    private fun getGanttChartData(id: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectRepo.getGanttChartData(id, authorization)
            }
            _ganttChartData.value = response
        }
    }

    private fun getProjectDueDate(id: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectRepo.getProject(id, authorization)
            }
            if(response is Resource.Success) {
                _dueDate.value = response.data?.dueDate
            } else {
                _dueDate.value = ""
            }
        }
    }

    fun getGanttChartDataAndProjectDueDate(id: String, authorization: String) {
        viewModelScope.launch {
            // Sử dụng supervisorScope để chạy song song
            supervisorScope {
                // Chạy song song hai hàm
                val ganttChartJob = launch {
                    getGanttChartData(id, authorization)
                }
                val projectDueDateJob = launch {
                    getProjectDueDate(id, authorization)
                }

                // Đợi cả hai coroutine hoàn thành
                ganttChartJob.join()
                projectDueDateJob.join()
            }
        }
    }

}