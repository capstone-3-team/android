package com.knu.quickthink.network

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userManager: UserManager
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val userState = userManager.userState.value
        Timber.d("userState : $userState")
        val authRequestBuilder = chain.request().newBuilder()
        if(userState.isLoggedIn){
            val userToken = runBlocking {
                withTimeoutOrNull(2000)    {
                    userManager.userState.value.userToken
                }
            }
            Timber.d("request userToken : $userToken")
            authRequestBuilder.addHeader("accessToken", "$userToken")
        }
        val authRequest = authRequestBuilder.build()

        val response = chain.proceed(authRequest)
        if(response.header("Authorization") != null){
            // token이 vaild할 경우 userToken를 저장해줘야함
//            sessionManager.setuserToken(response.header("Authorization")!!)
            Timber.tag("interceptor").d("header Authorization : ${response.header("Authorization")!!}")
        }
        // token이 invalid 하면  이미 response 객체에 오류가 붙어서 올거임-> NetworkCallAdapter가 그 뒤에 처리할거임
        return response
    }
}