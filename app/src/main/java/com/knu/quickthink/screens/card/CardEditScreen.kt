package com.knu.quickthink.screens.card

import android.app.Activity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.colorResource
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
import kotlinx.coroutines.delay
import timber.log.Timber


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CardEditScreen(
    viewModel: CardViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onDoneClicked: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val content by viewModel.content.collectAsState()
    val isContentEditing by viewModel.isContentEditing.collectAsState()
    val isPreview by viewModel.isPreview.collectAsState()
    val title by viewModel.title.collectAsState()
    val isKeyboardOpen by keyboardAsState()
    val scrollState = rememberScrollState()
    val interactionSource = remember { MutableInteractionSource() }
//    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current
    val lineHeightPx = with(LocalDensity.current) { 16.dp.toPx() }
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
//    Timber.tag("cardEdit").d("imeBottom $imeHeight")
    val focusManager = LocalFocusManager.current

    // 키보드 닫혔을 때 clearFocus && updateContent
    LaunchedEffect(isKeyboardOpen) {
        Timber.tag("cardEdit").d("LaunchedEffect isKeyboardOpen $isKeyboardOpen")
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
            viewModel.updateContent()
        }else{
            scrollState.animateScrollTo(
                cursorPositionToPixelOffset(content,content.selection.start,lineHeightPx),
            )
        }
    }
    // 커서 위치에 따라 스크롤 처리
    LaunchedEffect(content) {
        Timber.tag("cardEdit").d("LauncedEffect content.selection : ${content.selection} ")
        scrollState.animateScrollTo(
            cursorPositionToPixelOffset(content,content.selection.start,lineHeightPx),
        )
    }
    // 키보드에 따라 window크기 조절을 위해 필요한 부분으로 이해함
    val window = (context as? Activity)?.window
    if (window != null) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    val menuExpanded = remember { mutableStateOf(false) }
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            CardEditTopAppBar(
                isPreview = isPreview,
                menuExpanded = menuExpanded,
                onBackClicked = {
                    onBackClicked()
                },
                onPreviewEditClicked = {
                    viewModel.reverseIsPreview()
                },
                onDeleteClicked = {},
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(0.7f)
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .padding(
                        start = dimensionResource(id = R.dimen.horizontal_margin),
                        end = dimensionResource(id = R.dimen.horizontal_margin),
                        bottom = dimensionResource(id = R.dimen.vertical_margin),
                    )
                    .addFocusCleaner(keyboardController!!)
            ) {
                TextField(
                    modifier = Modifier.fillMaxWidth()
//                        .border(BorderStroke(2.dp,color =Color.LightGray) , shape = MaterialTheme.shapes.small)
                    ,
                    value = title,
                    textStyle = MaterialTheme.typography.h5,
                    enabled = !isPreview,
                    singleLine = true,
                    onValueChange = { viewModel.editTitle(it) },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.LightGray,
                        disabledTextColor = Color.Black,
                        disabledIndicatorColor = Color.LightGray
                    ),
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.vertical_margin)))
                Crossfade(
                    modifier = Modifier,
                    targetState = isPreview
                ) { targetState ->
                    when (targetState) {
                        false -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 2.dp,
                                        color = if (isContentEditing) Color.Blue else Color.LightGray,
                                        shape = MaterialTheme.shapes.small
                                    )
                            ) {
                                OutlinedTextField(
                                    value = content,
                                    modifier = Modifier
                                        .verticalScroll(scrollState)
                                        .imePadding()
//                                        .focusRequester(focusRequester)
                                        .onFocusChanged { focusState ->
                                            if(focusState.isFocused){
                                                viewModel.startEditing()
                                            }else{
                                                viewModel.finishEditing()
                                            }
                                        },
                                    onValueChange = { textFieldValue ->
                                        viewModel.editContent(textFieldValue)
                                    },
                                    placeholder = { Text("카드 내용을 입력해주세요") },
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        backgroundColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    shape = MaterialTheme.shapes.small
                                )
                            }
                        }
                        true -> {
                            RichText(
                                modifier = Modifier
                                    .verticalScroll(scrollState)
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

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.3f),
                onClick = onDoneClicked
            ) {
                Text(text = "Done")
            }

        }

    }

}

@Composable
fun CardEditTopAppBar(
    isPreview : Boolean,
    menuExpanded : MutableState<Boolean>,
    onBackClicked: () -> Unit,
    onPreviewEditClicked : () -> Unit,
    onDeleteClicked : () -> Unit,
) {
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick =onBackClicked) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "arrowBack"
                )
            }
        },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Crossfade(targetState = isPreview) { isPreview ->
                    IconButton(
                        onClick = onPreviewEditClicked
                    ){
                        if(isPreview){
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "edit",
                            )
                        }else{
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "show preview",
                            )
                        }
                    }
                }
//                Spacer(modifier = Modifier.width(5.dp))
                Box(){
                    IconButton(
                        onClick = {
                            menuExpanded.value = !menuExpanded.value
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "moreVert",
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                menuExpanded.value = false
                                onDeleteClicked()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(text = "Delete")
                        }
                    }
                }
            }
        },
        backgroundColor = colorResource(id = R.color.white),
        elevation = 0.dp,
    )
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
    val lines = textFieldValue.text.split("\n")                         // 가로 크기 계산해서 한 줄 넘어갔을 때도 split해줄 수 있다면 정말 베스트인데 일단 패스
    var offset = 0
    var line_num = 0

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
        line_num++
    }

    // Calculate the pixel offset based on line height
    return (line_num.toFloat() * lineHeightPx).toInt()
//    return (offset / lines.size.toFloat() * lineHeightPx).toInt()
}

@Preview(showBackground = true,)
@Composable
fun CardEditPrev() {
    Surface {
        CardEditScreen(onBackClicked = {}, onDoneClicked = {})
    }
}

@Preview(showBackground = true)
@Composable
fun CardEditTopAppBarPrev() {
    Surface() {
        val menuExpanded = remember {
            mutableStateOf(false)
        }
        val isPreView = remember {
            mutableStateOf(false)
        }
        CardEditTopAppBar(
            isPreview = false,
            menuExpanded = menuExpanded,
            onBackClicked = { /*TODO*/ },
            onPreviewEditClicked = { /*TODO*/ },
            onDeleteClicked = { /*TODO*/ },
        )
    }
}