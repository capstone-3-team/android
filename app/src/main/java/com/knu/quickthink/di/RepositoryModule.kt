package com.knu.quickthink.di

import com.knu.quickthink.repository.card.CardRepository
import com.knu.quickthink.repository.card.CardRepositoryImpl
import com.knu.quickthink.repository.chatGpt.ChatGptRepository
import com.knu.quickthink.repository.chatGpt.ChatGptRepositoryImpl
import com.knu.quickthink.repository.user.UserRepository
import com.knu.quickthink.repository.user.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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

    @Singleton
    @Binds
    abstract fun bindChatGptRepository(
        ChatGptRepositoryImpl: ChatGptRepositoryImpl
    ): ChatGptRepository

}