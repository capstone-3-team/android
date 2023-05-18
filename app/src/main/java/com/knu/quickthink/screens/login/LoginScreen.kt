package com.knu.quickthink.screens.login

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.knu.quickthink.components.GoogleLoginButton
import com.knu.quickthink.components.Logo
import com.knu.quickthink.components.LottieImage
import com.knu.quickthink.google.GoogleApiContract
import timber.log.Timber

@Composable
fun LoginScreen(
    viewModel: GoogleSignInViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onSignUpClicked: () -> Unit
) {
    val context = LocalContext.current
    val signInRequestCode = 1
    val isLoading by viewModel.isLoading.collectAsState()
    val isLogInSuccess by viewModel.isLogInSuccess.collectAsState()
    LaunchedEffect(isLogInSuccess) {
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

                Timber.tag("googleLogin").d("serverAuthCode : ${gsa?.serverAuthCode}")
                if (gsa != null) {
                    if (gsa.serverAuthCode == null || gsa.displayName == null || gsa.id == null || gsa.photoUrl == null) {
                        throw NullPointerException("GoogleSignInAccount Data null")
                    } else {
//                        Toast.makeText(context,"로그인 성공",Toast.LENGTH_SHORT).show()
                        viewModel.login(gsa)
                    }
                } else {
                    Toast.makeText(context,"구글 계정을 선택하세요",Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Timber.tag("googleLogin").e("Error in LoginScreen ApiException : $e")
                Toast.makeText(context,"구글 계정 정보 불러오기 오류",Toast.LENGTH_SHORT).show()
            }
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(isLoading){
            Timber.tag("isLoading").d("true")
            LottieImage(rawRes = R.raw.welcome, modifier = Modifier)
        }else {
            Logo()
            Spacer(modifier = Modifier.height(70.dp))
//            Box(
//                modifier = Modifier.height(70.dp),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = "서비스 사용을 위해 구글 로그인을 해주세요",
//                    style = MaterialTheme.typography.h5
//                )
//            }
            GoogleLoginButton(onLoginClicked = {
                authResultLauncher.launch(signInRequestCode)
            })
        }
    }
}

@Composable
fun showLoadingAnimation() {
    // Lottie 애니메이션을 표시하는 로직 등을 추가하세요
    LottieImage(rawRes = R.raw.welcome, modifier = Modifier)
}
