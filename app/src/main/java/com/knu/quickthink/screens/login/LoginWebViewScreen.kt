package com.knu.quickthink.screens.login

import android.annotation.SuppressLint
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.web.*
import com.knu.quickthink.di.BASE_URL
import com.knu.quickthink.di.NetworkModule.provideMyWebViewClient
import timber.log.Timber

const val NAVER_URL  = "www.naver.com"


@OptIn(ExperimentalLifecycleComposeApi::class)
@SuppressLint("SuspiciousIndentation")
@Composable
fun LoginWebViewScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onBackPressed : () -> Unit,
    onLoginSuccess : () -> Unit
) {
    val webChromeClient = AccompanistWebChromeClient()
    val webViewState = rememberWebViewState(url = LOGIN_START_URL)
    val webViewNavigator = rememberWebViewNavigator()

    val isLoginSuccess by viewModel.isLoginSuccess.collectAsState()
    val isLoading =viewModel.isLoading.collectAsState()

    LaunchedEffect(key1 = isLoading){
        viewModel.showLoading()
    }

    if(isLoginSuccess){
        Timber.tag("login").d("onLoginSccuess")
        viewModel.finishLogin()
        onLoginSuccess()
    }
//    LaunchedEffect(isLoginSuccess){
//    }

    WebView(
        state = webViewState,
        client = provideMyWebViewClient(viewModel),
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
    if(isLoading.value){
        Timber.tag("login").d("loading : ${isLoading.value}")
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            CircularProgressIndicator()
        }
    }
//    Timber.tag("login").d("launchedEffect : isLoginSuccess")
    BackHandler(enabled = true) {
        if(webViewNavigator.canGoBack){
            webViewNavigator.navigateBack()
        }else{
            onBackPressed()
        }
    }
}



//
//@Preview(showBackground = true)
//@Composable
//fun PrevLoginWebViewScreen() {
//    LoginWebViewScreen()
//}