package com.knu.quickthink.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.knu.quickthink.R
import com.knu.quickthink.components.GoogleLoginButton
import com.knu.quickthink.components.Logo

@Composable
fun LoginScreen(
    onLoginClicked: () -> Unit,
    onSignUpClicked: () -> Unit
) {
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
        GoogleLoginButton(onLoginClicked = onLoginClicked)
    }

}
