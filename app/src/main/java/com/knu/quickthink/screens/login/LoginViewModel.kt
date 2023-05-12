package com.knu.quickthink.screens.login

import android.graphics.Bitmap
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.web.AccompanistWebViewClient
import com.knu.quickthink.di.BASE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
const val LOGIN_START_URL  = "$BASE_URL/google.php"
const val LOGIN_FINISH_URL  = "$BASE_URL/auth.php"
@HiltViewModel
class LoginViewModel @Inject constructor() :ViewModel(){
    private val _isLoading  = MutableStateFlow(false)
    val isLoading :StateFlow<Boolean> = _isLoading

    private val _isLoginSuccess  = MutableStateFlow(false)
    val isLoginSuccess :StateFlow<Boolean> = _isLoginSuccess

    fun loginSuccess(){
        Timber.tag("login").d("viewmodel : loginSuccess() : ${_isLoginSuccess.value}")
        _isLoginSuccess.value = true
    }

    fun finishLogin(){
        _isLoginSuccess.value = false
    }

    fun showLoading(){
        _isLoading.value = true
    }
    fun hideLoading() {
        _isLoading.value = false
    }


}

class MyWebViewClient(
    private val loginViewModel: LoginViewModel
    ): AccompanistWebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url.toString()
        Timber.tag("login").d("shouldInterceptRequest : ${url}")
        if(url.contains(LOGIN_FINISH_URL)){
            Timber.tag("login").d("shouldInterceptRequest : true")
            val urlConnection = URL(url).openConnection() as HttpURLConnection
            try{
                val responseCode = urlConnection.responseCode
                Timber.tag("login").d("responseCode : $responseCode")
                if(responseCode == HttpURLConnection.HTTP_OK){
                    val inputStream = urlConnection.inputStream
                    val bytes = inputStream.readBytes()
                    Timber.tag("login").d("Response Body : ${bytes.decodeToString()}")
                    /* 여기서 이제 이걸 header에 넣어서 API호출 하고 값을 받아와서 유저 Interceptor에 저장해둬야함*/
                }
            }finally {
                urlConnection.disconnect()
            }
        }
        return super.shouldInterceptRequest(view, request)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        Timber.tag("login").d("hideLoading")
        loginViewModel.hideLoading()
    }

    // 페이지 로딩 완료 되었을 경우
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Timber.tag("login").d("url : $url view : $view")
        if(view != null && url?.contains(LOGIN_FINISH_URL) == true){
            Timber.tag("login").d("opPageFinished loginSuccess")
            loginViewModel.loginSuccess()
            view.visibility = View.GONE
//            loginViewModel.loginSuccess()
        }
    }


//    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//        if(view != null && request?.url.toString().contains("$BASE_URL/auth.php")){
//            onLoginSuccess()
////
////            val context = view.context
////            context.startActivity(Intent(context, MainActivity::class.java))
//            return true // 이벤트를 처리했으므로 true 반환
//        }
//        return super.shouldOverrideUrlLoading(view, request)
//    }

}