package com.example.youmanage.viewmodel.auth

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.youmanage.data.remote.authentication.AccessToken
import com.example.youmanage.data.remote.authentication.ChangePassword
import com.example.youmanage.data.remote.authentication.ResetPassword
import com.example.youmanage.data.remote.authentication.Email
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.data.remote.authentication.ResetToken
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
import com.example.youmanage.data.remote.authentication.VerifyRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.repository.AuthenticationRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: AuthenticationRepository
) : ViewModel() {

    private val _signUpResponse = MutableLiveData<Resource<UserSignUpResponse>>()
    val signUpResponse: LiveData<Resource<UserSignUpResponse>> get() = _signUpResponse

    private val _logInResponse = MutableLiveData<Resource<UserLogInResponse>>()
    val logInResponse: LiveData<Resource<UserLogInResponse>> get() = _logInResponse

    private val _logOutResponse = MutableLiveData<Resource<Message>>()
    val logOutResponse: LiveData<Resource<Message>> get() = _logOutResponse

    private val _verifyOTPResponse = MutableLiveData<Resource<Message>>()
    val verifyOTPResponse: LiveData<Resource<Message>> get() = _verifyOTPResponse

    private val _verifyResetPasswordOTP = MutableLiveData<Resource<ResetToken>>()
    val verifyResetPasswordOTP: LiveData<Resource<ResetToken>> get() = _verifyResetPasswordOTP

    private val _message = MutableLiveData<Resource<Message>>()
    val message: LiveData<Resource<Message>> get() = _message

    private val _user = MutableLiveData<Resource<User>>()
    val user : LiveData<Resource<User>> get() = _user

    private val _refreshResponse = MutableLiveData<Resource<AccessToken>>()
    val refreshResponse: LiveData<Resource<AccessToken>> get() = _refreshResponse

    val accessToken: Flow<String?> = repository.accessToken
    val refreshToken: Flow<String?> = repository.refreshToken

    private val supervisorJob = SupervisorJob() // Tạo SupervisorJob
    private val scope = CoroutineScope(Dispatchers.Main + supervisorJob) // Tạo CoroutineScope với SupervisorJob

    // Hàm đăng ký
    fun signUp(user: UserSignUp) {
        scope.launch {
            _signUpResponse.value = Resource.Loading(UserSignUpResponse("", ""))
            _signUpResponse.value = repository.signUp(user)
        }
    }

    // Hàm đăng nhập
    fun logIn(user: UserLogIn) {
        scope.launch {
            _logInResponse.value = repository.logIn(user)
        }
    }

    // Hàm đăng nhập với Google
    fun logInWithGoogle(user: UserGoogleLogIn) {
        scope.launch {
            _logInResponse.value = repository.logInWithGoogle(user)
        }
    }

    // Hàm xác nhận OTP
    fun verifyOTP(request: VerifyRequest){
        scope.launch {
            _verifyOTPResponse.value = repository.verifyOTP(request)
        }
    }

    // Hàm refresh access token
    fun refreshAccessToken(refreshToken: RefreshToken, authorization: String) {
        scope.launch {
            _refreshResponse.value = repository.refreshAccessToken(refreshToken, authorization)
        }
    }

    // Hàm xác nhận reset password OTP
    fun verifyResetPasswordOTP(request: VerifyRequest){
        scope.launch {
            _verifyResetPasswordOTP.value = repository.verifyResetPasswordOTP(request)
        }
    }

    // Hàm gửi OTP
    fun sendOTP(email: Email){
        scope.launch {
            repository.sendOPT(email)
        }
    }

    // Hàm kiểm tra email
    fun checkEmail(email: Email){
        scope.launch {
            _message.value = repository.checkEmail(email)
        }
    }

    // Hàm gửi OTP cho quên mật khẩu
    fun forgotPasswordSendOTP(email: Email){
        scope.launch {
            _verifyOTPResponse.value = repository.forgotPasswordSendOTP(email)
        }
    }

    // Hàm logout
    fun logOut(logoutRequest: RefreshToken, authorization: String) {
        scope.launch {
            _logOutResponse.value =
                repository.logOut(logoutRequest = logoutRequest, authorization = authorization)
        }
    }

    // Hàm reset mật khẩu
    fun resetPassword(request: ResetPassword){
        scope.launch {
            Log.d("changePassword", "changePassword: 1")
            _message.value = repository.resetPassword(request)
        }
    }

    // Hàm đổi mật khẩu
    fun changePassword(request: ChangePassword, authorization: String){
        scope.launch {
            Log.d("changePassword", "changePassword: 1")
            _message.value = repository.changePassword(request, authorization)
        }
    }

    // Hàm lưu token
    fun saveToken(
        accessToken: String,
        refreshToken: String,
        key1: Preferences.Key<String>,
        key2: Preferences.Key<String>
    ) {
        scope.launch {
            repository.saveToken(accessToken, refreshToken, key1, key2)
        }
    }

    // Hàm xóa token
    fun clearToken(key: Preferences.Key<String>) {
        scope.launch {
            repository.clearToken(key)
        }
    }

    // Hàm lấy thông tin người dùng
    fun getUser(authorization: String){
        scope.launch {
            _user.value = repository.getUser(authorization)
        }
    }


}
