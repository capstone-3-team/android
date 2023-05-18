package com.knu.quickthink.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.knu.quickthink.R
import com.knu.quickthink.network.networkResultCallAdapter.NetworkResultCallAdapterFactory
import com.knu.quickthink.network.nullOnEmptyConverter.NullOnEmptyConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.IOException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module{
    private const val BASE_URL = "https://api.quickthink.online"
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(NullOnEmptyConverterFactory)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResultCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            this.setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        return OkHttpClient.Builder()
//            .connectTimeout(100, TimeUnit.MILLISECONDS)
//            .readTimeout(100, TimeUnit.MILLISECONDS)
//            .writeTimeout(100, TimeUnit.MILLISECONDS)
            .addInterceptor(logging)
            .addNetworkInterceptor(authInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(
    ): AuthInterceptor {
        return AuthInterceptor()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context : Context) : GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(context.getString(R.string.gcp_web_client_id))
            .requestIdToken(context.getString(R.string.gcp_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }
}

class AuthInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val newRequest = chain.request().newBuilder()
//            .addHeader("Authorization", "Bearer $sessionId")
            .addHeader("Content-Type","application/json")
            .build()

        val response = chain.proceed(newRequest)
        if(response.header("Authorization") != null){
            // token이 vaild할 경우 sessionId를 저장해줘야함
//            sessionManager.setSessionId(response.header("Authorization")!!)
            Timber.tag("interceptor").d("header Authorization : ${response.header("Authorization")!!}")
        }
        // token이 invalid 하면  이미 response 객체에 오류가 붙어서 올거임-> NetworkCallAdapter가 그 뒤에 처리할거임
        return response
    }
}