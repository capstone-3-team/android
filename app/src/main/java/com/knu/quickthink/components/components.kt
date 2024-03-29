package com.knu.quickthink.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.knu.quickthink.R
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.airbnb.lottie.compose.*
import com.knu.quickthink.repository.user.DEFAULT_PROFILE_IMAGE
import com.knu.quickthink.screens.account.CircularAsyncImage
import com.knu.quickthink.utils.isURL


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
    isMainRoute : Boolean,
    menuExpanded : MutableState<Boolean>,
    onLogoClicked : () -> Unit,
    onSearchClicked : () -> Unit,
    onChatGPTClicked : () -> Unit,
    onAccountClicked : () -> Unit,
    onSignOutClicked : () -> Unit
){
    AnimatedVisibility(
        visible = isMainRoute,
        enter = slideInVertically(
            initialOffsetY = {fullHeight ->  -fullHeight},
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    )
    {
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
                                menuExpanded.value = !menuExpanded.value
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "settings",
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded.value,
                            onDismissRequest = { menuExpanded.value = false }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    menuExpanded.value = false
                                    onAccountClicked()
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
                                onClick = {
                                    menuExpanded.value = false
                                    onSignOutClicked()
                                }
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


}

@Composable
fun profileImage(
    imageUrl : String,
    imageSize : Dp
) {
    CircularAsyncImage(
        imageUrl = if(imageUrl.isURL()) imageUrl else DEFAULT_PROFILE_IMAGE,
        imageSize = imageSize,
        backgroundColor = Color.Transparent,
        contentDescription = "profile image"
    )
}

@Composable
fun LottieImage(rawRes : Int, modifier: Modifier) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(rawRes))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
    )
}

@Composable
fun CenterCircularProgressIndicator() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun FABContent(onTap: () -> Unit) {
    FloatingActionButton(
        onClick = onTap,
        shape = RoundedCornerShape(50.dp),
        backgroundColor  = colorResource(id = R.color.quickThink_blue)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Card",
            tint = Color.White
        )
    }
}
@Composable
fun CardDeleteConfirmDialog(
    onDeleteBtnClicked: () -> Unit,
    onCloseBtnClicked: () -> Unit
){
    DeleteConfirmDialog(
        title = "카드 삭제",
        text = "정말 카드를 삭제하시겠습니까?",
        onDeleteBtnClicked = onDeleteBtnClicked,
        onCloseBtnClicked = onCloseBtnClicked
    )
}

@Composable
fun DeleteConfirmDialog(
    title : String,
    text : String,
    onDeleteBtnClicked: () -> Unit,
    onCloseBtnClicked: () -> Unit
) {
    val buttonColors = ButtonDefaults.buttonColors(
        backgroundColor = Color.Transparent,
        contentColor = colorResource(id = R.color.quickThink_blue)
    )
    AlertDialog(
        onDismissRequest = onCloseBtnClicked,
        title = { Text(text = title,textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = { Text(text = text, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
//                    .width(350.dp)
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextButton(
                    onClick = onCloseBtnClicked,
                    colors = buttonColors
                ) {
                    Text("취소")
                }
                TextButton(
                    onClick = {
                        onDeleteBtnClicked()
                        onCloseBtnClicked()
                    },
                    colors = buttonColors
                ) {
                    Text("확인")
                }
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun CardDeleteConfrimDialogPrev() {
    Surface() {
        CardDeleteConfirmDialog(onDeleteBtnClicked = { /*TODO*/ }) {

        }

    }

}

//@Preview(showBackground = true)
//@Composable
//fun PrevTopAppBar() {
//    QuickThinkTopAppBar({},{})
//}
