package com.knu.quickthink.screens.card

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.knu.quickthink.R
import com.knu.quickthink.screens.main.addFocusCleaner
import timber.log.Timber


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardEditScreen(
    viewModel: CardViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val content by viewModel.content.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    val context = LocalContext.current

    DisposableEffect(Unit){
        val window = (context as? Activity)?.window
        if(window != null){
            WindowCompat.setDecorFitsSystemWindows(window,false)
        }
        onDispose {  }
    }
    val isKeyboardOpen by keyboardAsState()
    val focusRequester = remember{ FocusRequester() }

    /* isEditing true일 경우 텍스트필드에 focus 넣어주기 */
    LaunchedEffect(isEditing){
        Timber.tag("cardEdit").d("LaunchedEffect isEditing $isEditing")
        if(isEditing){
            focusRequester.requestFocus()
        }
    }

    LaunchedEffect(isKeyboardOpen){
        if (!isKeyboardOpen) {
            viewModel.updateContent()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(dimensionResource(id = R.dimen.vertical_margin))
            .addFocusCleaner(keyboardController!!)
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "제목",
            textStyle = MaterialTheme.typography.h5,
            onValueChange = {}
        )
        if (isEditing){
            TextField(
                value = content,
                onValueChange = { viewModel.editContent(it)},
                placeholder = { Text("카드 내용을 입력해주세요") },
                modifier = Modifier
                    .focusRequester(focusRequester)
            )
        }else{
            RichText(
                modifier = Modifier.clickable { viewModel.startEdit() }
            ) {
                Markdown(content = content)
            }
        }

    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Preview(showBackground = true)
@Composable
fun CardEditPrev() {
    Surface {
        CardEditScreen()
    }
}