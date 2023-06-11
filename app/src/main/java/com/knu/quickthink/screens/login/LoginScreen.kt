package com.knu.quickthink.screens.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.common.api.ApiException
import com.knu.quickthink.R
import com.knu.quickthink.components.CenterCircularProgressIndicator
import com.knu.quickthink.components.GoogleLoginButton
import com.knu.quickthink.components.Logo
import com.knu.quickthink.components.LottieImage
import com.knu.quickthink.google.GoogleApiContract
import timber.log.Timber

@Composable
fun LoginScreen(
    viewModel: GoogleSignInViewModel= hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    val context = LocalContext.current
    val signInRequestCode = 1                                               //증 작업의 결과를 처리하는 콜백에서 식별하기 위해 사용
    val isLoading by viewModel.isLoading.collectAsState()
    val isLogInSuccess by viewModel.isLogInSuccess.collectAsState()

    LaunchedEffect(isLogInSuccess) {
        Timber.tag("isLogInSuccess").d("loginSuccess $isLogInSuccess")
        if (isLogInSuccess) {
            Timber.tag("googleLogin").d("loginSuccess")
            onLoginSuccess()
        }
    }

    val authResultLauncher =
        rememberLauncherForActivityResult(contract = GoogleApiContract(viewModel.googleSignInClient)) { task ->
            try {
                Timber.tag("googleLogin").d("rememberLauncherForActivityResult")
                val gsa = task?.getResult(ApiException::class.java)
                Timber.tag("googleLogin").d("rememberLauncherForActivityResult : $gsa")

                if (gsa != null) {
                    viewModel.login(gsa)
//                        Toast.makeText(context,"로그인 성공",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context,"구글 계정을 선택하세요",Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Timber.tag("googleLogin").e("Error in LoginScreen ApiException : $e")
                Toast.makeText(context,"구글 계정 정보 불러오기 오류",Toast.LENGTH_SHORT).show()
            }
        }


    Crossfade(targetState = isLoading) {isLoading->
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(isLoading){
                Timber.tag("isLoading").d("true")
    //                CenterCircularProgressIndicator()
                LottieImage(rawRes = R.raw.welcome, modifier = Modifier)
            }else {
                Logo()
                Spacer(modifier = Modifier.height(70.dp))
                GoogleLoginButton(onLoginClicked = {
                    authResultLauncher.launch(signInRequestCode)
                })
            }
        }
    }
}
