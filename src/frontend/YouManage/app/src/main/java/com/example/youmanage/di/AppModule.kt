package com.example.youmanage.di

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.repository.AuthenticationRepository
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(api: ApiInterface) = AuthenticationRepository(api)

    @Provides
    @Singleton
    fun provideProjectManagementRepository(api: ApiInterface) = ProjectManagementRepository(api)
}