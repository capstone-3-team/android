package com.knu.quickthink.components

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.dokar.chiptextfield.ChipStyle
import com.dokar.chiptextfield.ChipTextFieldDefaults
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import timber.log.Timber


// com.dokar.chiptextfield 의 BasicTextField 그대로 가져왔습니다. Focus랑 입력관련 에러가 있어서 바로 처리하려고 가져옴. fork해서 수정하면 좋겠지만 일단 시간이없어서


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : HashTagChip> HashTagChipTextField(
    state: ChipTextFieldState<T>,
    onSubmit: (value: String) -> T?,
    modifier: Modifier = Modifier,
    innerModifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    readOnlyChips: Boolean = readOnly,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    textStyle: TextStyle = LocalTextStyle.current,
    chipStyle: ChipStyle = ChipTextFieldDefaults.chipStyle(),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    chipVerticalSpacing: Dp = 4.dp,
    chipHorizontalSpacing: Dp = 4.dp,
    chipLeadingIcon: @Composable (chip: T) -> Unit = {},
    chipTrailingIcon: @Composable (chip: T) -> Unit = {
        CloseButton(state,it)
    },
    onChipClick: ((chip: T) -> Unit)? = null,
    onChipLongClick: ((chip: T) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.TextFieldShape,
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    contentPadding: PaddingValues =
        if (label == null) {
            TextFieldDefaults.textFieldWithoutLabelPadding()
        } else {
            TextFieldDefaults.textFieldWithLabelPadding()
        }
) {
    var value by remember { mutableStateOf(TextFieldValue()) }
    val onValueChange: (TextFieldValue) -> Unit = {
        value = it
        Timber.tag("ChiptextField").d("onValueChange : {$it}")
    }
    Box(
        modifier = modifier
            .background(colors.backgroundColor(enabled).value, shape)
            .indicatorLine(enabled, isError, interactionSource, colors)
    ) {
        HashTagChipTextField(
            state = state,
            onSubmit = onSubmit,
            value = value,
            onValueChange = onValueChange,
            modifier = innerModifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            readOnlyChips = readOnlyChips,
            isError = isError,
            keyboardOptions = keyboardOptions,
            textStyle = textStyle,
            chipStyle = chipStyle,
            chipVerticalSpacing = chipVerticalSpacing,
            chipHorizontalSpacing = chipHorizontalSpacing,
            chipLeadingIcon = chipLeadingIcon,
            chipTrailingIcon = chipTrailingIcon,
            onChipClick = onChipClick,
            onChipLongClick = onChipLongClick,
            interactionSource = interactionSource,
            colors = colors,
            decorationBox = { innerTextField ->
                TextFieldDefaults.TextFieldDecorationBox(
                    value = if (state.chips.isEmpty() && value.text.isEmpty()) "" else " ",
                    innerTextField = innerTextField,
                    enabled = !readOnly,
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    isError = isError,
                    label = label,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    colors = colors,
                    contentPadding = contentPadding,
                )
            },
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun <T : HashTagChip> HashTagChipTextField(
    state: ChipTextFieldState<T>,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onSubmit: (value: String) -> T?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    readOnlyChips: Boolean = readOnly,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    textStyle: TextStyle = LocalTextStyle.current,
    chipStyle: ChipStyle = ChipTextFieldDefaults.chipStyle(),
    chipVerticalSpacing: Dp = 4.dp,
    chipHorizontalSpacing: Dp = 4.dp,
    chipLeadingIcon: @Composable (chip: T) -> Unit = {},
    chipTrailingIcon: @Composable (chip: T) -> Unit = { CloseButton(state, it) },
    onChipClick: ((chip: T) -> Unit)? = null,
    onChipLongClick: ((chip: T) -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: TextFieldColors = TextFieldDefaults.textFieldColors(),
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit =
        @Composable { innerTextField -> innerTextField() },
) {

    val scope = rememberCoroutineScope()

    val textFieldFocusRequester = remember { FocusRequester() }

    val editable = enabled && !readOnly

    val keyboardController = LocalSoftwareKeyboardController.current

//    val bringLastIntoViewRequester = remember { StableHolder(BringIntoViewRequester()) }

    val hasFocusedChipBeforeEmpty = remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        launch {
            snapshotFlow { state.focusedChip != null }
                .filter { state.chips.isNotEmpty() }
                .collect { hasFocusedChipBeforeEmpty.value = it }
        }
        snapshotFlow { state.chips }
            .filter { it.isEmpty() }
            .collect {
                if (hasFocusedChipBeforeEmpty.value) {
                    textFieldFocusRequester.requestFocus()
                }
                hasFocusedChipBeforeEmpty.value = false
            }
    }

    LaunchedEffect(state, state.disposed) {
        if (state.disposed) {
            state.chips = state.defaultChips
            state.disposed = false
        }
    }

    DisposableEffect(state) {
        onDispose {
            state.disposed = true
        }
    }

    decorationBox {
        com.google.accompanist.flowlayout.FlowRow(
            modifier = modifier
                .pointerInput(value) {
                    detectTapGestures(
                        onTap = {
                            if (!editable) return@detectTapGestures
                            Timber.tag("chipFocus").d("FlowRow detectTapGestures")
                            keyboardController?.show()
                            textFieldFocusRequester.requestFocus()
                            state.focusedChip = null
                            // Move cursor to the end
                            val selection = value.text.length
                            onValueChange(value.copy(selection = TextRange(selection)))
//                            scope.launch { bringLastIntoViewRequester.value.bringIntoView() }
                        },
                    )
                },
            mainAxisSpacing = chipHorizontalSpacing,
            crossAxisSpacing = chipVerticalSpacing,
            crossAxisAlignment = FlowCrossAxisAlignment.Center
        ) {
            val focuses = remember { StableHolder(mutableSetOf<FocusInteraction.Focus>()) }
            Chips(
                state = state,
                enabled = enabled,
                readOnly = readOnly || readOnlyChips,
                onRemoveRequest = { state.removeChip(it) },
                onFocused = {
                    Timber.tag("chipFocus").d("Chips onFocused의 it:$it")
                    if (!focuses.value.contains(it)) {
                        Timber.tag("chipFocus").d("Chips onFocused에서 tryEmit")
                        focuses.value.add(it)
                        interactionSource.tryEmit(it)                                                           // 여기서 interaction이  it으로 넘어온 값에 새로운 focus 이벤트 발행함
                        // 이러고 다음 동작이 어디로 가지? -> 이건 나중에 interactionSource를 LaunchedEffect로 불러서 다른 작업 추가해주는 용도임. 이 문제에서 상관 없음
                    }
                },
                onFreeFocus = {
                    focuses.value.remove(it)
                    interactionSource.tryEmit(FocusInteraction.Unfocus(it))
                },
                onLoseFocus = {
                    textFieldFocusRequester.requestFocus()
                    state.focusedChip = null
                },
                onChipClick = onChipClick,
                onChipLongClick = onChipLongClick,
                textStyle = textStyle,
                chipStyle = chipStyle,
                chipLeadingIcon = chipLeadingIcon,
                chipTrailingIcon = chipTrailingIcon,
//                bringLastIntoViewRequester = bringLastIntoViewRequester,
            )
            Timber.tag("ChipTextField").d("중간 HashTagChipTextField에서의 value : {$value}")
            Input(
                state = state,
                onSubmit = {
                    val chip = onSubmit(it.text)
                    if (chip != null) {
                        scope.launch {
                            awaitFrame()
//                            bringLastIntoViewRequester.value.bringIntoView()
                        }
                    }
                    chip
                },
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                isError = isError,
                textStyle = textStyle,
                colors = colors,
                keyboardOptions = keyboardOptions,
                focusRequester = textFieldFocusRequester,
                interactionSource = interactionSource,
                onFocusChange = { isFocused ->
                    if (isFocused) {
                        state.focusedChip = null
                    }
                },
                modifier = Modifier
//                    .bringIntoViewRequester(bringLastIntoViewRequester.value),
            )
        }
    }
}



@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
fun <T : HashTagChip> Chips(
    state: ChipTextFieldState<T>,
    enabled: Boolean,
    readOnly: Boolean,
    onRemoveRequest: (T) -> Unit,
    onFocused: (FocusInteraction.Focus) -> Unit,
    onFreeFocus: (FocusInteraction.Focus) -> Unit,
    onLoseFocus: () -> Unit,
    onChipClick: ((chip: T) -> Unit)?,
    onChipLongClick: ((chip: T) -> Unit)?,
    textStyle: TextStyle,
    chipStyle: ChipStyle,
    chipLeadingIcon: @Composable (chip: T) -> Unit,
    chipTrailingIcon: @Composable (chip: T) -> Unit,
//    bringLastIntoViewRequester: StableHolder<BringIntoViewRequester>,
) {
    val chips = state.chips

    val focusRequesters = remember(chips.size) {
        List(chips.size) { FocusRequester() }
    }

    fun focusChip(index: Int) {
        Timber.tag("chipFocus").d("Chips focusChip()")
        focusRequesters[index].requestFocus()
        val targetChip = chips[index]
        targetChip.textFieldValue = targetChip.textFieldValue.copy(
            selection = TextRange(targetChip.text.length),
        )
    }



    LaunchedEffect(chips, state.focusedChip) {
        Timber.tag("chipFocus").d("chips, state.focusedChip LaunchedEffect 내부")
        state.recordFocusedChip = true
        val chip = state.focusedChip ?: return@LaunchedEffect
        for (i in chips.indices) {
            if (chips[i] == chip) {
                Timber.tag("chipFocus").d("Chips LaunchedEffect state.focusedChip requestFocus()")
//                focusRequesters[i].requestFocus()
            // 여기서 실질적으로 해당 칩으로 Focus를 요청한다 // 그런데 이것도 주석처리해봤지만 이 문제랑 관련이 없다.. 이거 주석처리해도 focus가 자동으로 잡힌다
//                onFocused(chips[i].focus)                                                                       //이건 왜 날리는건지? 어차피 한 뎁스 위에서 if문에 걸러지는데
            }
        }
    }

    LaunchedEffect(chips) {
        snapshotFlow { state.nextFocusedChipIndex }
            .distinctUntilChanged()
            .filter { it != -1 }
            .collect { index ->
                Timber.tag("chipFocus").d("Chips LaunchedEffect state.nextFocusedChipIndex requestFocus()")
                if (index in 0..chips.lastIndex) {
                    focusRequesters[index].requestFocus()
                    onFocused(chips[index].focus)
                } else if (index != -1) {
                    if (chips.isNotEmpty()) {
                        focusRequesters[chips.lastIndex].requestFocus()
                    } else {
                        onLoseFocus()
                    }
                }
                state.nextFocusedChipIndex = -1
            }
    }

    for ((index, chip) in chips.withIndex()) {
        ChipItem(
            state = state,
            focusRequester = focusRequesters[index],
            chip = chip,
            enabled = enabled,
            readOnly = readOnly,
            onRemoveRequest = {
                // Call before removing chip
                onFreeFocus(chip.focus)
                if (chips.size > 1) {
                    focusChip((index - 1).coerceAtLeast(0))
                } else {
                    onLoseFocus()
                }
                onRemoveRequest(chip)
            },
            onFocusNextRequest = {
                onFreeFocus(chip.focus)
                if (index < chips.lastIndex) {
                    focusChip(index + 1)
                } else {
                    onLoseFocus()
                    focusRequesters[index].freeFocus()
                }
            },
            onFocusChange = { isFocused ->
                if (isFocused) {
                    Timber.tag("chipFocus").d("ChipItem onFocusChange() isFocused 내부 ")
                    if (state.recordFocusedChip) {                                              // ?? 이건 뭔데 -> 그냥 focusedChip 기록할건지 말건지
                        state.focusedChip = chip                                                // 여기서 focusedChip의 상태를 바꿔줘서 위의 LaunchedEffect에서 변화를 감지함
                    }
                    onFocused(chip.focus)                                                   // chip안에 Focusinteraction.Focus를 가지고 있음
                } else {
                    onFreeFocus(chip.focus)
                }
            },
            onClick = { onChipClick?.invoke(chip) },                                        // null이 아닐경우 함수 호출
            onLongClick = { onChipLongClick?.invoke(chip) },
            textStyle = textStyle,
            chipStyle = chipStyle,
            chipLeadingIcon = chipLeadingIcon,
            chipTrailingIcon = chipTrailingIcon,
            modifier =
//            if (index == chips.lastIndex) {
//                Modifier.bringIntoViewRequester(bringLastIntoViewRequester.value)
//            } else {
                Modifier
//            },
        )
    }
}

@ExperimentalComposeUiApi
@Composable
fun <T : HashTagChip> Input(
    state: ChipTextFieldState<T>,
    onSubmit: (value: TextFieldValue) -> T?,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean,
    readOnly: Boolean,
    isError: Boolean,
    textStyle: TextStyle,
    colors: TextFieldColors,
    keyboardOptions: KeyboardOptions,
    focusRequester: FocusRequester,
    interactionSource: MutableInteractionSource,
    onFocusChange: (isFocused: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Timber.tag("ChipTextField").d("Input의 value.text 체크하기 {${value.text}} ")
    if (value.text.isEmpty() && (!enabled || readOnly)) {
        return
    }
    val textColor = textStyle.color.takeOrElse {
        colors.textColor(enabled).value
    }

    fun tryAddNewChip(value: TextFieldValue): Boolean {
        return onSubmit(value)?.also { state.addChip(it) } != null
    }

    val isEditingOnDoneClicked =  remember { mutableStateOf(false)}

    BasicTextField(
        value = value,
        onValueChange = filterNewLines { newValue, hasNewLine, isEditing ->
            Timber.tag("ChipTextField").d("Input의 textField - filterNewLines - newValue :{${newValue.text}} hasNewLine : $hasNewLine ")
            if(isEditingOnDoneClicked.value) {
                isEditingOnDoneClicked.value = false
                return@filterNewLines
            }
            if (hasNewLine && newValue.text.isNotEmpty() && tryAddNewChip(newValue)) {
                Timber.tag("ChipTextField").d("Input의 textField 초기화됨")
                onValueChange(TextFieldValue())
            }
            else{
                Timber.tag("ChipTextField").d("Input의 textField $newValue 로 넣어짐")
                onValueChange(newValue)
            }
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { onFocusChange(it.isFocused) }
            .onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyDown && it.key == Key.Backspace) {                                        // 뒤로가기랑 뭐 다른거 눌렀을 때
                    if (value.text.isEmpty() && state.chips.isNotEmpty()) {
                        // Remove previous chip
                        state.removeLastChip()
                        return@onPreviewKeyEvent true
                    }
                }
                false
            },
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle.copy(color = textColor),
        keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                if (value.text.isNotEmpty() && value.text.isNotBlank() && tryAddNewChip(value)) {                               // isNotBlank 추가
                    Timber.tag("ChipTextField").d("Input : onDone text: ${value.text} ")
                    if(value.composition != null){                                                                              // onDone 눌렀을 때 한글 입력중인 상태라면 event 한 번 consume 해주기 위함
                        isEditingOnDoneClicked.value = true
                    }
                    onValueChange(TextFieldValue())
                }
            }
        ),
        interactionSource = interactionSource,
        cursorBrush = SolidColor(colors.cursorColor(isError).value),
    )
}


fun filterNewLines(
    block: (value: TextFieldValue, hasNewLine: Boolean, isEditing : Boolean) -> Unit
): (TextFieldValue) -> Unit = {
    val text = it.text
    val hasNewLine = text.hasNewLine()
    val isEditing = it.composition != null
    Timber.tag("ChipTextField").d("filterNewLines() text : $text")
    val value = if (hasNewLine) {
        TextFieldValue(
            text = text.removeNewLines(),
            selection = it.selection,
            composition = it.composition,
        )
    } else {
        it
    }
    block(value, hasNewLine,isEditing)
}

private fun String.hasNewLine(): Boolean {
    return indexOf('\n') != -1
}

private fun String.removeNewLines(): String {
    val index = indexOf('\n')
    return if (index != -1) {
        replace("\n", "")
    } else {
        this
    }
}

@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@Composable
private fun <T : HashTagChip> ChipItem(
    state: ChipTextFieldState<T>,
    focusRequester: FocusRequester,
    chip: T,
    enabled: Boolean,
    readOnly: Boolean,
    onRemoveRequest: () -> Unit,
    onFocusNextRequest: () -> Unit,
    onFocusChange: (isFocused: Boolean) -> Unit,
    onClick: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    textStyle: TextStyle,
    chipStyle: ChipStyle,
    chipLeadingIcon: @Composable (chip: T) -> Unit,
    chipTrailingIcon: @Composable (chip: T) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    val shape by chipStyle.shape(
        enabled = enabled,
        interactionSource = interactionSource,
    )

    val borderWidth by chipStyle.borderWidth(
        enabled = enabled,
        interactionSource = interactionSource,
    )

    val borderColor by chipStyle.borderColor(
        enabled = enabled,
        interactionSource = interactionSource,
    )

    val textColor by chipStyle.textColor(
        enabled = enabled,
        interactionSource = interactionSource,
    )
    val chipTextStyle = remember(textColor) { textStyle.copy(color = textColor) }

    val backgroundColor by chipStyle.backgroundColor(
        enabled = enabled,
        interactionSource = interactionSource,
    )

    val cursorColor by chipStyle.cursorColor()

    val keyboardController = LocalSoftwareKeyboardController.current

    val editable = enabled && !readOnly

    DisposableEffect(chip) {
        onDispose {
            val chips = state.chips
            if (!chips.contains(chip)) {
                // The current chip is removed, onFocusChanged() will not get called,
                // free the focus manually
                onFocusChange(false)
            }
        }
    }

    ChipItemLayout(
        leadingIcon = {
            chipLeadingIcon(chip)
        },
        trailingIcon = {
            chipTrailingIcon(chip)
        },
        modifier = modifier
            .clip(shape = shape)
            .background(color = backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = shape
            )
            .padding(borderWidth)
            .combinedClickable(
                enabled = enabled,
                onClick = {
                    if (editable) {
                        Timber.tag("chipFocus").d("ChipItemLayout.onClick()")
//                        keyboardController?.show()                                                                            // 딱히 필요없음. BasicTextField가 있잖아
//                        focusRequester.requestFocus()                                                                         //둘다 상관없는데
                    }
                    onClick?.invoke()
                },
                onLongClick = {
                    onLongClick?.invoke()
                }
            ),
    ) {
        var canRemoveChip by remember { mutableStateOf(false) }
        BasicTextField(
            value = chip.textFieldValue,
            onValueChange = filterNewLines { value, hasNewLine, isEditing->
                Timber.tag("ChipTextField").d("ChipItem의 BasicTextField의 onValueChange")
                chip.textFieldValue = value
                if (hasNewLine) {
                    onFocusNextRequest()
                }
            },
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(horizontal = 8.dp, vertical = 3.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    Timber.tag("chipFocus").d("ChipItem BasicTextField.onFocusChanged()")
                    onFocusChange(it.isFocused)
                }
                .onPreviewKeyEvent {
                    if (it.key == Key.Backspace) {
                        if (it.type == KeyEventType.KeyDown) {
                            canRemoveChip = chip.text.isEmpty()
                        } else if (it.type == KeyEventType.KeyUp) {
                            if (canRemoveChip) {
                                onRemoveRequest()
                                return@onPreviewKeyEvent true
                            }
                        }
                    }
                    false
                },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onFocusNextRequest() }),
            singleLine = false,
            enabled = !readOnly && enabled,
            readOnly = readOnly || !enabled,
            textStyle = chipTextStyle,
            interactionSource = interactionSource,
            cursorBrush = SolidColor(cursorColor),
        )
    }
}

@Composable
private fun ChipItemLayout(
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = {
            Box { leadingIcon() }
            Box { content() }
            Box { trailingIcon() }
        },
    ) { measurables, constraints ->
        val maxWidth = constraints.maxWidth

        val leadingMeasurable = measurables[0]
        val contentMeasurable = measurables[1]
        val trailingMeasurable = measurables[2]

        var restWidth = maxWidth

        val leadingPlaceable = leadingMeasurable.measure(constraints = constraints)
        restWidth -= leadingPlaceable.width

        val trailingPlaceable = trailingMeasurable.measure(Constraints(maxWidth = restWidth))
        restWidth -= trailingPlaceable.width

        val contentPlaceable = contentMeasurable.measure(Constraints(maxWidth = restWidth))

        val width = leadingPlaceable.width + contentPlaceable.width + trailingPlaceable.width
        val height = maxOf(
            leadingPlaceable.height,
            contentPlaceable.height,
            trailingPlaceable.height,
        )

        val placeables = arrayOf(leadingPlaceable, contentPlaceable, trailingPlaceable)

        layout(width = width, height = height) {
            var x = 0
            for (placeable in placeables) {
                placeable.placeRelative(
                    x = x,
                    y = (height - placeable.height) / 2,
                )
                x += placeable.width
            }
        }
    }
}


@Stable
data class StableHolder<T>(val value: T)

@Stable
class ChipTextFieldState<T : HashTagChip>(
    chips: List<T> = emptyList()
) {
    internal var disposed = false

    internal var defaultChips: List<T> = chips

    internal var focusedChip: T? by mutableStateOf(null)

    internal var nextFocusedChipIndex by mutableStateOf(-1)

    internal var recordFocusedChip = true

    var chips by mutableStateOf(chips)

    /**
     * Add a chip
     */
    fun addChip(chip: T) {
        val list = chips.toMutableList()
        list.add(chip)
        chips = list
    }

    /**
     * Remove chip
     */
    fun removeChip(chip: T) {
        val list = chips.toMutableList()
        val index = list.indexOf(chip)
        if (index == -1) {
            return
        }

        val focusedChipIndex = list.indexOf(focusedChip)
        if (focusedChipIndex == list.lastIndex && focusedChipIndex > 0) {
            if (index == list.lastIndex) {
                // The chip to remove is also the last chip, change the focused chip to the
                // previous chip
                focusedChip = list[index - 1]
            }
            // IME will lose focus if the focused chip is the last, we move the focus to the
            // previous composable to avoid IME flash
            recordFocusedChip = false
            nextFocusedChipIndex = focusedChipIndex - 1
        } else if (chip == focusedChip && index < list.lastIndex) {
            // We are removing the focusing chip, simply set the focused chip to the next one.
            focusedChip = list[index + 1]
        }

        list.remove(chip)
        chips = list
    }

    internal fun removeLastChip() {
        val list = chips.subList(0, chips.size - 1)
        chips = list
    }
}

@Composable
fun <T : HashTagChip> CloseButton(
    state: com.knu.quickthink.components.ChipTextFieldState<T>,
    chip: T,
    modifier: Modifier = Modifier,
    backgroundColor: Color = if (MaterialTheme.colors.isLight) {
        Color.Black.copy(alpha = 0.3f)
    } else {
        Color.White.copy(alpha = 0.3f)
    },
    strokeColor: Color = Color.White,
    startPadding: Dp = 0.dp,
    endPadding: Dp = 6.dp
) {
    Row(
        modifier = modifier
            .padding(start = startPadding, end = endPadding)
    ) {
        CloseButtonImpl(
            onClick = { state.removeChip(chip) },
            backgroundColor = backgroundColor,
            strokeColor = strokeColor
        )
    }
}

@Composable
private fun CloseButtonImpl(
    onClick: () -> Unit,
    backgroundColor: Color,
    strokeColor: Color,
    modifier: Modifier = Modifier,
) {
    val padding = with(LocalDensity.current) { 6.dp.toPx() }
    val strokeWidth = with(LocalDensity.current) { 1.2.dp.toPx() }
    val viewConfiguration = LocalViewConfiguration.current
    val viewConfigurationOverride = remember(viewConfiguration) {
        ViewConfigurationOverride(
            base = viewConfiguration,
            minimumTouchTargetSize = DpSize(24.dp, 24.dp)
        )
    }
    CompositionLocalProvider(LocalViewConfiguration provides viewConfigurationOverride) {
        Canvas(
            modifier = modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(backgroundColor)
                .clickable(onClick = onClick)
        ) {
            drawLine(
                color = strokeColor,
                start = Offset(padding, padding),
                end = Offset(size.width - padding, size.height - padding),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = strokeColor,
                start = Offset(padding, size.height - padding),
                end = Offset(size.width - padding, padding),
                strokeWidth = strokeWidth
            )
        }
    }
}

internal class ViewConfigurationOverride(
    base: ViewConfiguration,
    override val doubleTapMinTimeMillis: Long = base.doubleTapMinTimeMillis,
    override val doubleTapTimeoutMillis: Long = base.doubleTapTimeoutMillis,
    override val longPressTimeoutMillis: Long = base.longPressTimeoutMillis,
    override val touchSlop: Float = base.touchSlop,
    override val minimumTouchTargetSize: DpSize = base.minimumTouchTargetSize
) : ViewConfiguration


@Stable
open class HashTagChip(text: String) {
    internal var textFieldValue by mutableStateOf(TextFieldValue(text))

    internal val focus = FocusInteraction.Focus()

    val text: String get() = textFieldValue.text
}


@Composable
fun <T : HashTagChip> rememberChipTextFieldState(
    chips: List<T> = emptyList()
): ChipTextFieldState<T> {
    return remember { ChipTextFieldState(chips = chips) }
}