package com.example.youmanage.data.remote

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthenticateAPI {

    @POST("auth/signup/")
    suspend fun signUp(
        @Body user: UserSignUp
    ): UserSignUpResponse

    @POST("auth/login/")
    suspend fun logIn(
        @Body user: UserLogIn
    ): UserLogInResponse

    @POST("auth/google-login/")
    suspend fun logInWithGoogle(
        @Body user: UserGoogleLogIn
    ): UserLogInResponse

    @POST("auth/logout/")
    suspend fun logOut(
        @Body logoutRequest: RefreshToken,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String
    ): Message

    @GET("auth/test_token/")
    suspend fun testRefreshToken(
        @Query("refresh") refresh: String,
        @Header("Authorization") authorization: String
    ): String

    @POST("auth/reset_password/")
    suspend fun changePassword(
        @Body request: ChangePassword,
        @Header("Authorization") authorization: String
    ): Message

    @POST("auth/token/refresh/")
    suspend fun refreshAccessToken(
        @Body logout: RefreshToken,
        @Header("Authorization") authorization: String
    ): AccessToken

    @POST("/auth/forgot_password/check_email/")
    suspend fun checkEmail(
        @Body email: Email
    ): Message

    @POST("auth/email_auth/verify/")
    suspend fun verifyOTP(
        @Body verifyRequest: VerifyRequest
    ): Message

    @POST("/auth/email_auth/send_otp/")
    suspend fun sendOTP(
        @Body email: Email
    ): Message

    @POST("/auth/forgot_password/send_otp/")
    suspend fun forgotPasswordSendOTP(
        @Body email: Email
    ): Message

    @POST("/auth/forgot_password/change_password/")
    suspend fun resetPassword(
        @Body request: ResetPassword,
    ): Message

    @POST("/auth/forgot_password/verify_otp/")
    suspend fun verifyResetPasswordOTP(
        @Body refresh: VerifyRequest
    ): ResetToken

}