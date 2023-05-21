package com.knu.quickthink.screens.card

import android.app.Activity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val isPreview by viewModel.isPreview.collectAsState()
    val context = LocalContext.current

    // 키보드에 따라 window크기 조절을 위해 필요한 부분으로 이해함
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
        onDispose { }
    }
    val isKeyboardOpen by keyboardAsState()
    val focusRequester = remember { FocusRequester() }
    val scrollState = rememberScrollState()
    val lineHeightPx = with(LocalDensity.current) { 24.dp.toPx() }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
//    Timber.tag("cardEdit").d("imeBottom $imeHeight")
    val interactionSource = remember { MutableInteractionSource() }
    val focusManager = LocalFocusManager.current


    LaunchedEffect(isEditing) {
        Timber.tag("cardEdit").d("LaunchedEffect isEditing $isEditing")
        if (isEditing) {
            /* isEditing true일 경우 텍스트필드에 focus 넣어주기 */
//            focusRequester.requestFocus()
//            scrollState.animateScrollTo(cursorPositionToPixelOffset(content,content.selection.start,lineHeightPx))    // 적용 잘 안됨
        }
    }

    /*
    * TODO : View로 돌아가기 버튼 있어야할 듯 탭으로 주거나(github PR 쓰는거처럼)
    * 현재는 커서 위치 정하고 수정한 후에 키보드 내리면 자동저장되는 시스템
    * 커서 위치 무조건 정해야 나갈 수 있는데 커서 위치 정하기 전에 돌아갈 수 있도록 만드는게 좋다
    * */

    LaunchedEffect(isKeyboardOpen) {
        Timber.tag("cardEdit").d("LaunchedEffect isKeyboardOpen $isKeyboardOpen")
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
            viewModel.updateContent()
        }
        if(isKeyboardOpen){
            scrollState.animateScrollTo(cursorPositionToPixelOffset(content,content.selection.start,lineHeightPx))
        }
    }

    LaunchedEffect(content) {
        Timber.tag("cardEdit").d("LauncedEffect content.selection : ${content.selection} ")
        scrollState.animateScrollTo(
            cursorPositionToPixelOffset(
                content,
                content.selection.start,
                lineHeightPx
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(dimensionResource(id = R.dimen.vertical_margin))
            .imePadding()
            .addFocusCleaner(keyboardController!!)
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.reverseIsPreview() }
        ) {
            Text(text = "preview")
        }
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = "제목",
            textStyle = MaterialTheme.typography.h5,
            enabled = !isPreview,
            onValueChange = {}
        )
        Crossfade(
            modifier = Modifier.verticalScroll(scrollState),
            targetState = isPreview
        ) { targetState ->
            when (targetState) {
                false -> {
                    OutlinedTextField(
                        value = content,
                        onValueChange = { textFieldValue ->
                            viewModel.editContent(textFieldValue)
                        },
                        placeholder = { Text("카드 내용을 입력해주세요") },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    viewModel.startEditing()
                                }
                            }
                    )
                }
                true -> {
                    RichText(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null                                            // 클릭 꾹 누르면 검은색으로 변하는 상호작용 없애주기 위함
                            ) {
                                viewModel.reverseIsPreview()
                            }
                    ) {
                        Markdown(content = content.text)
                    }
                }
            }
        }
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

private fun cursorPositionToPixelOffset(
    textFieldValue: TextFieldValue,
    cursorPosition: Int,
    lineHeightPx: Float
): Int {
    val lines = textFieldValue.text.split("\n")

    var offset = 0

    for (element in lines) {
//        Timber.tag("cardEdit").d("element : $element")
        offset += element.length

        if (offset >= cursorPosition) {
            // Subtract the length of the current line from the offset
            // to get the offset within the line itself
            offset -= element.length
            break
        }

        // Add 1 to account for the line break character
        offset++
    }

    // Calculate the pixel offset based on line height
    return (offset / lines.size.toFloat() * lineHeightPx).toInt()
}

@Preview(showBackground = true)
@Composable
fun CardEditPrev() {
    Surface {
        CardEditScreen()
    }
}