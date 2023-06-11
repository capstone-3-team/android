package com.knu.quickthink.di

import com.knu.quickthink.network.apiService.CardApiService
import com.knu.quickthink.network.apiService.ChatGptApiService
import com.knu.quickthink.network.apiService.UserApiService
import com.knu.quickthink.network.networkResultCallAdapter.NetworkResultCallAdapterFactory
import com.knu.quickthink.repository.card.CardRepository
import com.knu.quickthink.repository.card.CardRepositoryImpl
import com.knu.quickthink.repository.user.UserRepository
import com.knu.quickthink.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideCardApiService(retrofit: Retrofit): CardApiService {
        return retrofit.create(CardApiService::class.java)
    }

}
