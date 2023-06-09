package com.knu.quickthink.di

import com.knu.quickthink.network.apiService.CardApiService
import com.knu.quickthink.network.apiService.UserApiService
import com.knu.quickthink.repository.card.CardRepository
import com.knu.quickthink.repository.card.CardRepositoryImpl
import com.knu.quickthink.repository.user.UserRepository
import com.knu.quickthink.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindAuthRepository(
        UserRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Singleton
    @Binds
    abstract fun bindCardRepository(
        CardRepositoryImpl: CardRepositoryImpl
    ): CardRepository

}