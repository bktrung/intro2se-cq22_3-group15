package com.example.youmanage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
import com.example.youmanage.repository.AuthenticationRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: AuthenticationRepository
): ViewModel() {

    private val _signUpResponse = MutableLiveData<Resource<UserSignUpResponse>>()
    val signUpResponse: LiveData<Resource<UserSignUpResponse>> get() = _signUpResponse

    private val _logInResponse = MutableLiveData<Resource<UserLogInResponse>>()
    val logInResponse: LiveData<Resource<UserLogInResponse>> get() = _logInResponse

    fun signUp(user: UserSignUp) {
        viewModelScope.launch {
            _signUpResponse.value = repository.signUp(user)
        }
    }

    fun logIn(user: UserLogIn) {
        viewModelScope.launch {
            _logInResponse.value = repository.logIn(user)
        }
    }

    fun logInWithGoogle(user: UserGoogleLogIn){
        viewModelScope.launch {
            _logInResponse.value = repository.logInWithGoogle(user)
        }
    }

    fun logOut(logoutRequest: RefreshToken, authorization: String) {
        viewModelScope.launch {
           val response = repository.logOut(logoutRequest = logoutRequest, authorization = authorization)

            if(response is Resource.Success){
                Log.d("LogOut", "Success ${response.data?.message.toString()}")
            }
            if(response is Resource.Error){
                Log.d("Logout", "Error ${response.message.toString()}")
            }
        }
    }
}