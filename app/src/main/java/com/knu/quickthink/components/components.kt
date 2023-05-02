package com.knu.quickthink.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.knu.quickthink.R


@Composable
fun Logo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.quickthink_logo_1000)
                .crossfade(true)
                .build(),
        )
        Image(
            painter = painter,
            contentDescription = "Splash Img"
        )
        if (painter.state is AsyncImagePainter.State.Loading ||
            painter.state is AsyncImagePainter.State.Error
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun GoogleLoginButton(
    onLoginClicked : () -> Unit
) {
    OutlinedButton(
        modifier = Modifier.height(40.dp),
        onClick = onLoginClicked,
        elevation = ButtonDefaults.elevation(4.dp)
    ) {
        Image(
            modifier = Modifier.padding(start = 8.dp,end = 24.dp),
            painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(R.drawable.google_logo)
                    .crossfade(true)
                    .build(),
            ),
            contentDescription = "google_icon"
        )
        Text(
            text = "SIGN IN WITH GOOGLE",
            fontSize = 14.sp,
            fontFamily =  FontFamily.Default,
            fontWeight = FontWeight.Medium,
            color = Color.Black.copy(0.54f)
        )
    }
}

