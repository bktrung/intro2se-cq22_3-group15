package com.example.youmanage.di

import android.content.Context
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.repository.AuthenticationRepository
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.repository.TaskManagementRepository
import com.example.youmanage.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {


    @Provides
    @Singleton
    fun provideCookieJar(): CookieJar {
        return object : CookieJar {
            private val cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()

            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                cookieStore[url.host] = cookies
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore[url.host] ?: emptyList()
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cookieJar: CookieJar): OkHttpClient {
        return OkHttpClient.Builder()
            .cookieJar(cookieJar) // Tự động quản lý cookie
            .addInterceptor { chain ->
                val request = chain.request()
                println("Request: ${request.headers}")
                val response = chain.proceed(request)
                println("Response: ${response.headers}")
                response
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(okHttpClient: OkHttpClient): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(api: ApiInterface, @ApplicationContext context: Context) = AuthenticationRepository(api, context)

    @Provides
    @Singleton
    fun provideProjectManagementRepository(api: ApiInterface) = ProjectManagementRepository(api)

    @Provides
    @Singleton
    fun provideTaskManagementRepository(api: ApiInterface) = TaskManagementRepository(api)
}