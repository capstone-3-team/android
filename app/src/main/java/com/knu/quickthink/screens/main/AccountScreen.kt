package com.knu.quickthink.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.knu.quickthink.R
import timber.log.Timber

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AccountScreen(
    onCloseClicked: () -> Unit,
    viewModel: AccountViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val profileImage by viewModel.profileImage.collectAsState()
    val googleId by viewModel.googleId.collectAsState()
    val introduction by viewModel.introduction.collectAsState()
    val isUpdateSuccess by viewModel.isUpdateSuccess.collectAsState()

    LaunchedEffect(isUpdateSuccess){
        if (isUpdateSuccess) onCloseClicked()
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(dimensionResource(id = R.dimen.vertical_margin))
            .addFocusCleaner(keyboardController!!)
    ) {
        Row(
            modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.vertical_margin)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularAsyncImage(
                imageUrl = profileImage,
                imageSize = dimensionResource(id = R.dimen.profile_image),
                backgroundColor = Color.Transparent,
                contentDescription = "profile image"
            )
            Text(
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin)),
                text = googleId,
                style = MaterialTheme.typography.h6
            )
        }

        val textFieldColors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = colorResource(id = R.color.quickThink_blue)
        )

        OutlinedTextField(
            value = introduction,
            onValueChange = viewModel::editIntroduction,
            placeholder = { Text("자기소개를 입력해주세요") },
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth()
                .shadow(elevation = 3.dp)
                .clip(RoundedCornerShape(10.dp)),
            colors = textFieldColors,
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController.hide()
                }
            )
        )
        Button(
            modifier = Modifier.padding(top = dimensionResource(id = R.dimen.vertical_margin)),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.quickThink_blue)
            ),
            onClick = {
                viewModel.updateIntroduction()
            }
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Edit", style = MaterialTheme.typography.h6, color = Color.White
                )
            }
        }
    }
}

@Composable
fun CircularAsyncImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    imageSize: Dp,
    backgroundColor: Color = MaterialTheme.colors.primary,
    contentDescription: String? = null
) {
    Box(
        modifier = modifier
            .size(imageSize)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )

    }

}


@ExperimentalComposeUiApi
fun Modifier.addFocusCleaner(
    keyboardController: SoftwareKeyboardController,
    doOnClear: () -> Unit = {}
): Modifier {
    return this.pointerInput(Unit) {
        detectTapGestures(onTap = {
            Timber.tag("addFocus").e("Touch")
            doOnClear()
            keyboardController.hide()
        })
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 500)
@Composable
fun PrevAcocuntScreen() {
    Surface {
        AccountScreen({})
    }

}