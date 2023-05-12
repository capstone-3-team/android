package com.knu.quickthink.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.knu.quickthink.R
import androidx.compose.ui.res.vectorResource


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

@Composable
fun QuickThinkTopAppBar(
    onLogoClicked : () -> Unit,
    onSearchClicked : () -> Unit,
    onChatGPTClicked : () -> Unit,
    onAccountClicked : () -> Unit,
    onSignOutClicked : () -> Unit
){
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(R.drawable.quickthink_logo)
                        .crossfade(true)
                        .build(),
                )
                Image(
                    painter = painter,
                    contentDescription = "logo Img",
                    modifier = Modifier.clickable {
                        onLogoClicked()
                    }
//                    modifier = Modifier.size(30.dp)
                )
                if (painter.state is AsyncImagePainter.State.Loading ||
                    painter.state is AsyncImagePainter.State.Error
                ) {
                    CircularProgressIndicator()
                }
                Spacer(modifier = Modifier.fillMaxWidth())
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onSearchClicked
                ){
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "search",
                    )
                }
//                Spacer(modifier = Modifier.width(5.dp))

                IconButton(
                    modifier = Modifier.background(Color.White),
                    onClick = onChatGPTClicked
                ){
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.outline_chat_24),
                        contentDescription = "chatgpt",
                        modifier = Modifier.size(20.dp),
                    )
                }
//                Spacer(modifier = Modifier.width(5.dp))
                Box(){
                    IconButton(
                        onClick = {
                            menuExpanded = !menuExpanded
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "settings",
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                onAccountClicked()
                                menuExpanded = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "account",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "Account")
                        }
                        DropdownMenuItem(
                            onClick = onSignOutClicked
                        ) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_exit_to_app_24),
                                contentDescription = "sign out",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "Sign Out")
                        }
                    }
                }
            }
        },
        backgroundColor = colorResource(id = R.color.white),
        elevation = 0.dp,
    )
}


//@Preview(showBackground = true)
//@Composable
//fun PrevTopAppBar() {
//    QuickThinkTopAppBar({},{})
//}
