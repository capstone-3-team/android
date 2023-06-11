package com.knu.quickthink.di

import com.knu.quickthink.BuildConfig
import com.knu.quickthink.BuildConfig.OPENAI_BASE_URL
import com.knu.quickthink.network.AuthInterceptor
import com.knu.quickthink.network.UserManager
import com.knu.quickthink.network.apiService.ChatGptApiService
import com.knu.quickthink.network.networkResultCallAdapter.NetworkResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatGptModule {

    @Provides
    @Singleton
    fun provideChatGptApiService(
        chatGptInterceptor :ChatGptInterceptor
    ) : ChatGptApiService {
        val logging = HttpLoggingInterceptor().apply {
            this.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addNetworkInterceptor(chatGptInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(OPENAI_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
            .build()

        return retrofit.create(ChatGptApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideChatGptInterceptor(): ChatGptInterceptor {
        return ChatGptInterceptor()
    }
}

class ChatGptInterceptor : Interceptor {
    private val openAiToken = "Bearer ${BuildConfig.OPENAI_KEY}"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization",openAiToken)
            .build()

        return chain.proceed(newRequest)
    }
}