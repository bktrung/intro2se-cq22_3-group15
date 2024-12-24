package com.example.youmanage.viewmodel

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.youmanage.repository.ChangeRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangeRequestViewModel @Inject constructor(
    private val repository: ChangeRequestRepository
): ViewModel(){

}