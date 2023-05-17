package com.knu.quickthink.screens.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.api.ApiException
import com.knu.quickthink.components.GoogleLoginButton
import com.knu.quickthink.components.Logo
import com.knu.quickthink.google.GoogleApiContract
import timber.log.Timber

@Composable
fun LoginScreen(
    viewModel : GoogleSignInViewModel = hiltViewModel(),
    onLoginSuccess : () -> Unit,
    onSignUpClicked: () -> Unit
) {
    val signInRequestCode = 1
    val isLogInSuccess = viewModel.isLogInSuccess.collectAsState()
    val isError = remember{
        mutableStateOf(false)
    }

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = GoogleApiContract()){task ->
            try{
                Timber.tag("googleLogin").d("rememberLauncherForActivityResult")
                val gsa = task?.getResult(ApiException::class.java)
                Timber.tag("googleLogin").d("rememberLauncherForActivityResult : $gsa")
                if(gsa != null){
                    if(gsa.idToken == null || gsa.displayName == null || gsa.id == null || gsa.photoUrl == null){
                        throw NullPointerException("GoogleSignInAccount Data null")
                    }else{
                        viewModel.fetchSignInUser(
                            token = gsa.idToken!!,
                            googleName = gsa.displayName!!,
                            googleId = gsa.id!!,
                            profilePicture = gsa.photoUrl.toString()
                        )
                        viewModel.login()
                    }
                }else{
                    isError.value = true
                }
            }catch(e : ApiException){
                Timber.tag("googleLogin").e("Error in LoginScreen ApiException : $e")
            }
        }
    LaunchedEffect(isLogInSuccess){
        if(isLogInSuccess.value){
            Timber.tag("googleLogin").d("loginSuccess")
            viewModel.hideLoading()
            onLoginSuccess()
            viewModel.loginFinished()
        }
    }
        // 이걸 안해주면 recomposition이 무제한으로 일어남  왜이런거지? MutableStateFlow때문인가?
//        viewModel.loginFinish()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Logo()
        Box(modifier = Modifier.height(70.dp),
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "서비스 사용을 위해 구글 로그인을 해주세요",
                style = MaterialTheme.typography.h5
            )
        }
        GoogleLoginButton(onLoginClicked = {
            authResultLauncher.launch(signInRequestCode)
        })
    }

}

