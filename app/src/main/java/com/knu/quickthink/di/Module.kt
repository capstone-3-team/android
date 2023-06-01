package com.knu.quickthink.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.knu.quickthink.R
import com.knu.quickthink.network.AuthInterceptor
import com.knu.quickthink.network.UserManager
import com.knu.quickthink.network.networkResultCallAdapter.NetworkResultCallAdapterFactory
import com.knu.quickthink.network.nullOnEmptyConverter.NullOnEmptyConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        userManager: UserManager
    ): AuthInterceptor {
        return AuthInterceptor(userManager)
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context : Context) : GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(context.getString(R.string.gcp_web_client_id))
            .requestIdToken(context.getString(R.string.gcp_web_client_id))                                  // 이거 받아야지만 프로필 이미지 넘어온다
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    @Singleton
    @Provides
    fun provideUserManager() : UserManager {
        return UserManager()
    }

}
