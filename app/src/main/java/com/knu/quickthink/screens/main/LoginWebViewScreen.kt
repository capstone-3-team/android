package com.knu.quickthink.screens.main

import android.annotation.SuppressLint
import android.content.Intent
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.Navigation.findNavController
import com.google.accompanist.web.*
import com.google.gson.Gson
import com.knu.quickthink.MainActivity
import com.knu.quickthink.R
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL

const val BASE_URL  = "http://52.78.233.108/quickThink"

const val LOGIN_FINISH_URL  = "$BASE_URL/google.php"
const val LOGIN_START_URL  = "$BASE_URL/auth.php"
const val NAVER_URL  = "www.naver.com"


@SuppressLint("SuspiciousIndentation")
@Composable
fun LoginWebViewScreen(
    onBackPressed : () -> Unit,
    onLoginSuccess : () -> Unit
) {
    val webViewClient = MyWebViewClient(onLoginSuccess)
    val webChromeClient = AccompanistWebChromeClient()
    val webViewState = rememberWebViewState(url = "$BASE_URL/google.php")
    val webViewNavigator = rememberWebViewNavigator()

    WebView(
        state = webViewState,
        client = webViewClient,
        navigator = webViewNavigator,
        onCreated = { webView ->
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true
        },
        onDispose = { view ->
            view.stopLoading()
            view.loadUrl("about:blank")
            view.clearHistory()
            view.clearCache(true)
            view.destroy()
            Timber.tag("login").d("Webview destory")
        }
    )
    BackHandler(enabled = true) {
        if(webViewNavigator.canGoBack){
            webViewNavigator.navigateBack()
        }else{
            onBackPressed()
        }
    }
}

/* TODO : 받아온 response 어딘가에 저장해둬야함 */

class MyWebViewClient(private val onLoginSuccess : () -> Unit): AccompanistWebViewClient() {

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        val url = request?.url.toString()
        Timber.tag("login").d("shouldInterceptRequest : ${url}")
        if(url.contains("$BASE_URL/auth.php")){
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

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        Timber.tag("login").d("url : $url")
        if(view != null && url?.contains("$BASE_URL/auth.php") == true){
            onLoginSuccess()
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

//
//@Preview(showBackground = true)
//@Composable
//fun PrevLoginWebViewScreen() {
//    LoginWebViewScreen()
//}