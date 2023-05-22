package com.knu.quickthink.screens.card

import android.app.Activity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.knu.quickthink.R
import com.knu.quickthink.components.HashTagTextField
import com.knu.quickthink.screens.main.addFocusCleaner
import com.knu.quickthink.screens.main.testCard
import timber.log.Timber


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun CardEditScreen(
    viewModel: CardViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onDoneClicked: () -> Unit
) {
    val content by viewModel.content.collectAsState()
    val extractedText = extractTextUntilCursor(content)
    val isContentEditing by viewModel.isContentEditing.collectAsState()
    val isPreview by viewModel.isPreview.collectAsState()
    val title by viewModel.title.collectAsState()
    val isKeyboardOpen by keyboardAsState()
    val scrollState = rememberScrollState()
    val interactionSource = remember { MutableInteractionSource() }
    var isHashTagTextFieldFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current


    val keyboardController = LocalSoftwareKeyboardController.current
    val bringIntoViewRequester = remember { BringIntoViewRequester() }                      // ExperimentalFoundationApi임, adjustResize manifest에 무조건 추가해줘야함
    val coroutineScope = rememberCoroutineScope()
    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
//    Timber.tag("cardEdit").d("imeBottom $imeHeight")

    val contentTextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        textAlign = TextAlign.Start
    )
    val lineHeightPx = with(LocalDensity.current) { 20.sp.toPx() }

    /* TODO: 밑에 들어가는 컨텐츠 크기만큼 유동적으로 패딩 설정  일단은 임시로 값 넣음*/
    val contentImePadding = if (!isHashTagTextFieldFocused) {
        WindowInsets.ime.asPaddingValues(LocalDensity.current)
            .calculateBottomPadding()
            .coerceAtMost(200.dp)
    } else 0.dp
    val hashTagImePadding = if (isHashTagTextFieldFocused) {
        WindowInsets.ime.asPaddingValues(LocalDensity.current)
            .calculateBottomPadding()
            .coerceAtMost(100.dp)
    } else 0.dp
    Timber.tag("ime").d("${contentImePadding}")
    Timber.tag("ime").d("hasTagClicked :${isHashTagTextFieldFocused}")

    // 키보드 닫혔을 때 clearFocus && updateContent
    LaunchedEffect(isKeyboardOpen) {
        Timber.tag("cardEdit").d("LaunchedEffect isKeyboardOpen $isKeyboardOpen")
        if (!isKeyboardOpen) {
            focusManager.clearFocus()
            viewModel.updateContent()
        }
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
                    )
//                    .addFocusCleaner(keyboardController!!)
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
                    modifier = Modifier
                        .padding(bottom = contentImePadding),
                    targetState = isPreview
                ) { targetState ->
                    when (targetState) {
                        false -> {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        width = 2.dp,
                                        color = if (isContentEditing) Color.Blue else Color.LightGray,
                                        shape = MaterialTheme.shapes.small
                                    )
                            ) {
                                val horizontalPadding = with(LocalDensity.current){
                                    2 * dimensionResource(id = R.dimen.horizontal_margin).toPx().toInt()
                                }
                                val paragraph = calculateParagraph(
                                    text = extractedText,
                                    maxWidth = constraints.maxWidth - horizontalPadding ,
                                    textStyle = contentTextStyle
                                )
//                                Timber.tag("paragraph").d("${paragraph.lineCount}")
//                                Timber.tag("paragraph").d("${paragraph.height}")
                                /* 커서 위치에 따라 스크롤 처리 */
                                LaunchedEffect(content){
                                    scrollState.animateScrollTo(
                                        (paragraph.lineCount-1) * lineHeightPx.toInt()
                                    )
                                }
                                Column(
                                    modifier = Modifier.fillMaxSize()
                                ){
                                    OutlinedTextField(
                                        value = content,
                                        modifier = Modifier
                                            .verticalScroll(scrollState)
                                            .focusRequester(focusRequester)
                                            .onFocusChanged { focusState ->
                                                if (focusState.isFocused) {
                                                    viewModel.startEditing()
                                                } else {
                                                    viewModel.finishEditing()
                                                }
                                            },
                                        onValueChange = { textFieldValue ->
                                            viewModel.editContent(textFieldValue)
                                        },
                                        textStyle = contentTextStyle,
                                        placeholder = { Text("카드 내용을 입력해주세요") },
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            backgroundColor = Color.Transparent,
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent
                                        ),
                                        shape = MaterialTheme.shapes.small
                                    )
                                    Box(
                                        Modifier.fillMaxSize()
                                            .clickable(
                                                interactionSource = interactionSource,
                                                indication = null
                                            ){
                                                focusRequester.requestFocus()
                                            }
                                    ){

                                    }

                                }
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
        Column(
            modifier = Modifier

//                  .then(if(isHashTagTextFieldFocused)Modifier.imePadding() else Modifier)
        ) {
//                if(!isContentEditing){
            HashTagTextField(
                card = testCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
                    .padding(bottom = hashTagImePadding)
                    /* TODO : HashTagTextField를 클릭했을 때 키보드가 올라오는데 카보드 바로 위에 텍스트 필드가 있었으면 좋겠음 */

//                            .bringIntoViewRequester(bringIntoViewRequester)
                    .onFocusEvent { focusState ->
                        Timber
                            .tag("focus")
                            .d("focusState.isFocused : ${focusState.isFocused}")
                        Timber
                            .tag("focus")
                            .d("focusState.hasFocus : ${focusState.hasFocus}")
                        /*TODO : 포커스처리만 해줘야함  */
                        isHashTagTextFieldFocused =
                            focusState.isFocused || focusState.hasFocus
//                                if (focusState.isFocused) {
//                                    isHashTagTextFieldFocused = true
//                                    coroutineScope.launch {
//                                        bringIntoViewRequester.bringIntoView()
//                                    }
//                                }
                    },
                onChipClicked = { _, _ -> },
                onDeleteButtonClicked = { _, _ -> }
            )
//                }
        }
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.1f)
                .padding(dimensionResource(id = R.dimen.vertical_margin))
        ) {
            Text(
                text = "Done",
                style = MaterialTheme.typography.h6,
                color = Color.White
            )
        }
    }

    }
}

/**
 * textField의 정보에 따라 Paragrah (속성) 반환
 * */
@Composable
fun calculateParagraph(
    text: String,
    maxWidth : Int,
    textStyle : TextStyle
) :Paragraph = Paragraph(
    paragraphIntrinsics = ParagraphIntrinsics(
        text = text,
        style = textStyle,
        density = LocalDensity.current,
        fontFamilyResolver = createFontFamilyResolver(LocalContext.current)
    ),
    constraints = Constraints(
        minWidth = 0,
        maxWidth = maxWidth,
        minHeight = 0,
        maxHeight = Constraints.Infinity
    )
)

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

fun extractTextUntilCursor(textFieldValue: TextFieldValue): String {
    val text = textFieldValue.text
    val selectionStart = textFieldValue.selection.start
    return text.substring(0, selectionStart)
}


@Preview(showBackground = true,)
@Composable
fun CardEditPrev() {
    Surface {
        CardEditScreen(onBackClicked = {}, onDoneClicked = {})
    }
}

//@Preview(showBackground = true)
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