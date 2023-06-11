package com.knu.quickthink.screens.main

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knu.quickthink.R
import com.knu.quickthink.model.chatGPT.dummyMessageDataList
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowCompat
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.knu.quickthink.screens.card.CardEditScreen
import timber.log.Timber

@Composable
fun ChatGptScreen(
    viewModel: ChatGptViewModel = hiltViewModel(),
    onBackPressed : () -> Unit,
//    onMessageSaved: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if(uiState.showCardEditDialog){
        if(uiState.savedCardId != null){
            CardEditDialog(
                cardId = uiState.savedCardId!!,
                onDismissRequest = {
                    viewModel.updateUiState(uiState.copy(showCardEditDialog = false))
                    Toast.makeText(context,"카드가 저장되었습니다",Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    ChatGptContent(
        messages = uiState.messageDataList,
        isMessageEmpty = uiState.messageDataList.isEmpty(),
        isChatGptTyping = uiState.isChatGptTyping,
        onMessageSent = { message ->
            viewModel.askToGpt(message)
        },
        onMessageSaved = { message ->
            viewModel.saveMessage(message)
        },
        onBackPressed = onBackPressed
    )

}

@Composable
fun ChatGptContent(
    messages: List<MessageData>,
    isMessageEmpty: Boolean,
    isChatGptTyping: Boolean,
    onBackPressed : () -> Unit,
    onMessageSent: (String) -> Unit,
    onMessageSaved: (String) -> Unit
) {
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit){
        val window = (context as? Activity)?.window
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }
    val bottom = WindowInsets.ime.getBottom(LocalDensity.current)
    Timber.d("ime bottom : $bottom")

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = { ChatGptTopAppBar(
            onBackPressed = onBackPressed
        )},
        containerColor = Color.White,
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
            .exclude(WindowInsets.ime),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ChatGptInfoBox(
                isMessageEmpty = isMessageEmpty,
                isChatGptTyping = isChatGptTyping
            )
            Messages(
                messages = messages,
                navigateToProfile = {  },
                modifier = Modifier
                    .weight(1f),
                scrollState = scrollState,
                onMessageSaved = onMessageSaved
            )
            UserInput(
                onMessageSent = onMessageSent,
                modifier = Modifier
                    .imePadding()
                ,
                onTextFieldFocused = {focused ->
                    if(focused){
                        scope.launch {
                            scrollState.scrollToItem(0)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ChatGptInfoBox(
    isMessageEmpty : Boolean,
    isChatGptTyping : Boolean,
) {
    Crossfade(targetState = isChatGptTyping || isMessageEmpty) { showBox ->
        if(showBox){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
        //                .background(colorResource(id = R.color.quickThink_blue).copy(0.3f))
                ,
                contentAlignment = Alignment.Center
            ){
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if(isMessageEmpty){
                        Text("Send a message.")
                    }
                    if(isChatGptTyping){
                        Text("ChatGPT Typing...")
                    }
                }
            }
        }
    }
}

@Composable
fun UserInput(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier,
    onTextFieldFocused: (Boolean) -> Unit,
) {
    var textState by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    var textFieldFocusState by remember { mutableStateOf(false) }
    val roundedCornerShape = RoundedCornerShape(dimensionResource(id = R.dimen.dialog_roundedCorner))
    BasicTextField(
        value = textState,
        onValueChange = {textState = it},
        modifier = modifier,
        maxLines = 1,
        cursorBrush = SolidColor(colorResource(id = R.color.quickThink_blue))
    ) { innerTextField ->
        Surface(modifier = modifier
            .padding(4.dp),
            color = Color.Transparent,
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .height(40.dp)
                ,
                verticalAlignment = Alignment.CenterVertically
            ){
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White,                                                      // 이렇게 해주면 Transparent가 elevation때문에 회색으로 나옴..
                    elevation = 2.dp,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = 8.dp)
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        innerTextField()
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if(textState.text.isNotEmpty()){
                            onMessageSent(textState.text)
                            textState = TextFieldValue()
                        }
                    },
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(roundedCornerShape)
                    ,
                    interactionSource = remember{ MutableInteractionSource() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.quickThink_blue),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "전송", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun Messages(
    messages: List<MessageData>,
    navigateToProfile: (String) -> Unit,
    scrollState: LazyListState,
    modifier: Modifier = Modifier,
    onMessageSaved: (String) -> Unit
) {
    Box(modifier = modifier.fillMaxWidth()) {
        LazyColumn(
            reverseLayout = true,
            state = scrollState,
            modifier = Modifier.fillMaxSize()
        ){
            items(messages){ message ->
                if(message.isUserMe){
                    MyMessageBubble(msg = message.messageRequest.content)
                }else{
                    ChatGptMessageBubble(
                        msg = message.messageRequest.content,
                        onMessageSaved = onMessageSaved
                    )
                }
            }
        }
    }
}

@Composable
fun MyMessageBubble(
    msg: String,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val myMessageBubbleShape = RoundedCornerShape(12.dp, 4.dp, 12.dp, 12.dp)
    Column (
        modifier = Modifier
            .padding(dimensionResource(id = R.dimen.horizontal_margin))
            .fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ){
        MessageTextBox(
            msg =  msg,
            shape = myMessageBubbleShape,
            onTextLayout = onTextLayout
        )
    }
}

@Composable
fun ChatGptMessageBubble(
    msg: String,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onMessageSaved: (String) -> Unit
) {
    val chatGptMessageBubbleShape = RoundedCornerShape(4.dp, 12.dp, 12.dp, 12.dp)
    Box(
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
            .fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ){
        Column (Modifier.width(IntrinsicSize.Max)){
            MessageTextBox(
                msg =  msg,
                modifier = Modifier.widthIn(max = 300.dp),
                shape = chatGptMessageBubbleShape,
                onTextLayout = onTextLayout
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ){
                Button(
                    onClick = { onMessageSaved(msg) },
                    shape = RoundedCornerShape(dimensionResource(id = R.dimen.dialog_roundedCorner)),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = colorResource(id = R.color.quickThink_blue)
                    ),
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_bookmark_border_24),
                        contentDescription = "messageSave",
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.Bottom)
                    )
                    Text(
                        text = "카드에 저장하기",
                    )
                }
            }
        }
    }
}


@Composable
fun MessageTextBox(
    msg : String,
    modifier: Modifier = Modifier,
    shape: Shape,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    Surface(
        shape = shape,
        elevation = 2.dp,
//        color = colorResource(id = R.color.quickThink_blue).copy(0.3f),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.padding(16.dp)
        ){
            BasicText(
                text = msg,
                modifier = modifier,
                style = TextStyle.Default,
                softWrap = true,
                overflow = TextOverflow.Clip,
                maxLines = Int.MAX_VALUE,
                onTextLayout = {
                    layoutResult.value = it
                    onTextLayout(it)
                }
            )

        }
    }
}

@Composable
fun ChatGptTopAppBar(
    onBackPressed: () -> Unit,
) {
    val menuExpanded = remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text("ChatGPT에게 물어보기",style = MaterialTheme.typography.h6) },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "arrowBack"
                )
            }
        },
        backgroundColor = colorResource(id = R.color.white),
        elevation = 0.dp,
    )
}

@Composable
fun CardEditDialog(
    cardId : Long,
    onDismissRequest : () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
            ,decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            CardEditScreen(
                cardId = cardId,
                onBackClicked = onDismissRequest,
                onDoneClicked = {}
            )
        }
    }

}

//@Preview(showBackground = true)
@Composable
fun UserInputPrev() {
    UserInput(onMessageSent = {}, onTextFieldFocused = {})
}

//@Preview(showBackground = true)
//@Composable
//fun MessageBubblePrev() {
//
//}


@Preview(showBackground = true)
@Composable
fun MessagesPrev() {
    Messages(
        messages = dummyMessageDataList,
        navigateToProfile = {},
        scrollState = rememberLazyListState(),
        onMessageSaved = {}
    )
}
@Preview(showBackground = true)
@Composable
fun ChatGptContentPrev() {
    Surface {
        ChatGptContent(
            messages = dummyMessageDataList,
            isMessageEmpty = false,
            isChatGptTyping = true,
            onMessageSent = {},
            onMessageSaved = {},
            onBackPressed = {}
        )
    }
}