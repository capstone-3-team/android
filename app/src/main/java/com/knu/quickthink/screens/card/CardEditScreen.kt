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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.knu.quickthink.components.CardDeleteConfirmDialog
import com.knu.quickthink.components.CenterCircularProgressIndicator
import com.knu.quickthink.components.HashTagTextField
import com.knu.quickthink.utils.convertDateFormat
import timber.log.Timber


@Composable
fun CardEditScreen(
    cardId : Long,
    viewModel: CardEditViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onDoneClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isKeyboardOpen by keyboardAsState()
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var firstRendering by remember { mutableStateOf(true) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit){
        viewModel.fetchMyCard(cardId)
        // 키보드에 따라 window크기 조절을 위해 필요한 부분으로 이해함
        val window = (context as? Activity)?.window
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
    // 키보드 닫혔을 때 clearFocus && updateContent
    LaunchedEffect(isKeyboardOpen) {
        Timber.tag("cardEdit").d("LaunchedEffect isKeyboardOpen $isKeyboardOpen")
        if (!isKeyboardOpen && !firstRendering) {
            focusManager.clearFocus()
            viewModel.updateCard()
        }else firstRendering = false
    }

    LaunchedEffect(uiState.isDeleted){
        if(uiState.isDeleted){
            onBackClicked()
        }
    }

    if(showDeleteConfirmDialog){
        CardDeleteConfirmDialog(
            onDeleteBtnClicked = { viewModel.deleteCard() },
            onCloseBtnClicked = { showDeleteConfirmDialog = false }
        )
    }

//    LaunchedEffect(uiState.myCard, uiState.content){
//        Timber.d("myCard : ${uiState.myCard}")
//        Timber.d("content : ${uiState.content.text}")
//    }

    if(uiState.isLoading){
        CenterCircularProgressIndicator()
    }else{
        Scaffold(
            modifier = Modifier
                .systemBarsPadding()
                .navigationBarsPadding(),
            topBar = {
                CardEditTopAppBar(
                    isPreview = uiState.isPreview,
                    onBackClicked = {
                        viewModel.updateCard()
                        onBackClicked()
                    },
                    onPreviewEditClicked = { viewModel.reverseIsPreview() },
                    onDeleteClicked = { showDeleteConfirmDialog = true },
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues)
                    .padding(
                        start = dimensionResource(id = R.dimen.horizontal_margin),
                        end = dimensionResource(id = R.dimen.horizontal_margin)
                    ),
    //                    .addFocusCleaner(keyboardController!!)
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                CardTitle(
                    title = uiState.myCard.title,
                    isPreview = uiState.isPreview
                ){
                    viewModel.updateUiStateOfCard(uiState.myCard.copy(title = it))
                }
                HashTagTextField(
                    hashTags = uiState.myCard.hashTags,
                    modifier = Modifier.fillMaxWidth(),
//                    enabled = uiState.isPreview,
                    readOnly = uiState.isPreview,
                    onChipUpdated = {
                        Timber.d("onChipUpdated : $it")
                        viewModel.updateUiStateOfCard(uiState.myCard.copy(hashTags = it.toHashSet()))
                    },
                    onChipClicked = { _, _ -> },
                    onChipDeleteClicked = { _, _ -> }
                )
                CardContent(
                    modifier = Modifier,
                    uiState = uiState,
                    focusRequester = focusRequester,
    //                    contentImePadding = contentImePadding,
                    onFocusChanged = {focusState ->
                        if(focusState.isFocused || focusState.hasFocus) viewModel.startEditing()
                        else viewModel.finishEditing()
                    },
                    onValueChange = {
                        viewModel.updateUiState(uiState.copy(content = it))
//                        viewModel.updateUiStateOfCard(uiState.myCard.copy(content = it.text))
                    },
                    onContentClicked = { viewModel.reverseIsPreview() }
                )
            }
        }
    }
}

@Composable
fun CardContent(
    modifier :Modifier = Modifier,
    uiState: CardEditUiState,
    focusRequester: FocusRequester,
//    contentImePadding :Dp,
    onFocusChanged : (FocusState) -> Unit,
    onValueChange: (TextFieldValue) -> Unit,
    onContentClicked : () -> Unit
) {
    val extractedText = extractTextUntilCursor(uiState.content)
    val scrollState = rememberScrollState()
    val lineHeightPx = with(LocalDensity.current) { 20.sp.toPx() }
    val interactionSource = MutableInteractionSource()
    val contentTextStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        textAlign = TextAlign.Start
    )
    Column(modifier = modifier
        .fillMaxSize()
        .imePadding()
        .padding(vertical = dimensionResource(id = R.dimen.vertical_margin))
    ) {
        Crossfade(
            modifier = modifier.weight(0.93f),
    //            .padding(bottom = contentImePadding)
            targetState = uiState.isPreview
        ) { targetState ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = if (uiState.isContentEditing) Color.Blue else Color.LightGray,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                when (targetState) {
                    true -> {
                        RichText(
                            modifier = Modifier
                                .padding(dimensionResource(id = R.dimen.horizontal_margin))
                                .verticalScroll(scrollState)
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,                                            // 클릭 꾹 누르면 검은색으로 변하는 상호작용 없애주기 위함,
                                    onClick = onContentClicked
                                )
                        ) {
                            Markdown(content = uiState.content.text)
                        }
                    }
                    false -> {
                            val horizontalPadding = with(LocalDensity.current){
                                2 * dimensionResource(id = R.dimen.horizontal_margin).toPx().toInt()
                            }
                            /* 현재 커서가 몇 번째 라인인지 찾기*/
                            val paragraph = calculateParagraph(
                                text = extractedText,
                                maxWidth = constraints.maxWidth - horizontalPadding ,
                                textStyle = contentTextStyle
                            )
                            /* 커서 위치에 따라 스크롤 처리 */
                            LaunchedEffect(uiState.content){
                                scrollState.animateScrollTo(
                                    (paragraph.lineCount-1) * lineHeightPx.toInt()
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                            ){
                                OutlinedTextField(
                                    value = uiState.content,
                                    modifier = Modifier
                                        .verticalScroll(scrollState)
                                        .focusRequester(focusRequester)
                                        .onFocusChanged(onFocusChanged),
                                    onValueChange = onValueChange,
                                    textStyle = contentTextStyle,
                                    placeholder = { Text(stringResource(id = R.string.default_card_content)) },
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        backgroundColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    shape = MaterialTheme.shapes.small
                                )
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .clickable(
                                            interactionSource = interactionSource,
                                            indication = null
                                        ) {
                                            focusRequester.requestFocus()
                                        }
                                )
                                        Timber.tag("paragraph").d("${paragraph.lineCount}")
                                        Timber.tag("paragraph").d("${paragraph.height}")
                            }
                        }
                    }
                }
        }
        CardStatusRow(
            modifier = modifier
                .fillMaxWidth()
                .weight(0.07f)
            ,
            reviewCount = uiState.myCard.reviewCount,
            writtenDate = convertDateFormat(uiState.myCard.writtenDate),
            latestReviewDate = convertDateFormat(uiState.myCard.latestReviewDate)
        )
    }
}


@Composable
fun CardStatusRow(
    modifier: Modifier = Modifier,
    reviewCount : Long,
    writtenDate : String,
    latestReviewDate : String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,){
            Icon(
                painter = painterResource(id = R.drawable.baseline_visibility_24),
                contentDescription = "preview",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(reviewCount.toString())
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "writtenDate",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(writtenDate)
        }
        Row(verticalAlignment = Alignment.CenterVertically,){
            Icon(
                painter = painterResource(id = R.drawable.baseline_add_task_24),
                contentDescription = "latestReviewDate",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(latestReviewDate)
        }
    }
}

@Composable
fun CardTitle(
    title : String,
    isPreview: Boolean,
    onValueChange: (String) -> Unit
) {
    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = title,
        textStyle = MaterialTheme.typography.h5,
        enabled = !isPreview,
        singleLine = true,
        onValueChange = onValueChange,
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.Transparent,
            focusedIndicatorColor = Color.Blue,
            unfocusedIndicatorColor = Color.LightGray,
            disabledTextColor = Color.Black,
            disabledIndicatorColor = Color.LightGray
        ),
    )
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
    onBackClicked: () -> Unit,
    onPreviewEditClicked : () -> Unit,
    onDeleteClicked : () -> Unit,
) {
    val menuExpanded = remember { mutableStateOf(false) }
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
                                painter = painterResource(id = R.drawable.baseline_preview_24),
                                contentDescription = "preview"
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
        CardEditScreen(-1,onBackClicked = {}, onDoneClicked = {})
    }
}

@Preview(showBackground = true,)
@Composable
fun CardStatusPrev() {
    Surface() {
        CardStatusRow(
            reviewCount = 3,
            writtenDate = "2020.3.3",
            latestReviewDate = "2023.3.3"
        )
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
            onBackClicked = { /*TODO*/ },
            onPreviewEditClicked = { /*TODO*/ },
            onDeleteClicked = { /*TODO*/ },
        )
    }
}


//    var isHashTagTextFieldFocused by remember { mutableStateOf(false) }
//    val keyboardController = LocalSoftwareKeyboardController.current
//    val bringIntoViewRequester = remember { BringIntoViewRequester() }                      // ExperimentalFoundationApi임, adjustResize manifest에 무조건 추가해줘야함
//    val coroutineScope = rememberCoroutineScope()
//    val imeHeight = WindowInsets.ime.getBottom(LocalDensity.current)
//    Timber.tag("cardEdit").d("imeBottom $imeHeight")


/* 밑에 들어가는 컨텐츠 크기만큼 유동적으로 패딩 설정  일단은 임시로 값 넣음*/
//    val contentImePadding = if (!isHashTagTextFieldFocused) {
//        WindowInsets.ime.asPaddingValues(LocalDensity.current)
//            .calculateBottomPadding()
//            .coerceAtMost(230.dp)
//    } else 0.dp
//
//    Timber.tag("contentImePadding").d("contentImePadding : ${contentImePadding}")

