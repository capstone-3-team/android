package com.knu.quickthink.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dokar.chiptextfield.*
import com.knu.quickthink.R
import com.knu.quickthink.model.card.dummyHashTags
import timber.log.Timber

@Composable
fun HashTagTextField(
    hashTags: HashSet<String>,
    modifier: Modifier = Modifier,
    enabled : Boolean = true,
    readOnly : Boolean = false,
    onChipUpdated : (List<String>) -> Unit,
    onChipClicked: (String, Boolean) -> Unit,
    onChipDeleteClicked: (String, Boolean) -> Unit
) {
    val state = rememberChipTextFieldState(chips = hashTags.map { HashTagChip(it)}.toList())
    val interactionSource = remember {
        MutableInteractionSource()
    }

    LaunchedEffect(interactionSource ){
        interactionSource.interactions.collect(){interaction ->
            when(interaction){
                is FocusInteraction.Focus -> {
                    Timber.tag("focus").d("FocusInteraction.Focus 날리고 싶다 제발")                               // 이거랑 상관없었다,,
                }
            }

        }
    }

    HashTagChipTextField(
        state = state,
        onSubmit = {
            // 새로운 해시태그 추가됐을 경우 업데이트
            Timber.d("onSubmit $it")
            val chips = state.chips.map { chip -> chip.text }
            onChipUpdated(chips + it)                                                                 // 실제 chips의 값이 업데이트 되는건 나중이므로 여기서 바로 +it을 통해서 viewmodel의 값 업데이트한다
            HashTagChip(it)
        },
        onChipEditDone = {
            // 기존 해시태그 수정됐을 경우 업데이트
            val chips = state.chips.map { it.text }
            onChipUpdated(chips)
            Timber.d("onChipEditDone chips : $chips")
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        placeholder = {
            Text(text = "해시태그를 입력해 주세요")
        },
        chipStyle = ChipTextFieldDefaults.chipStyle(
            unfocusedBackgroundColor = colorResource(id = R.color.quickThink_blue),
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        ),
        onChipClick = { chip ->
            Timber.d("onChipClicked : $onChipClicked  chip: $chip")
            onChipClicked(chip.text,false) },
        chipTrailingIcon = {
            Image(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(18.dp)
                    .clickable {
                        state.removeChip(it)
                        val chips = state.chips.map { it.text }
                        onChipUpdated(chips)
                    },
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        interactionSource = interactionSource,
        contentPadding = PaddingValues(
            vertical = dimensionResource(id = R.dimen.vertical_margin),
            horizontal = 0.dp
        )
    )
}


@Preview(showBackground = true)
@Composable
fun HashTagTextFieldPrev(){
    Column() {
        Spacer(modifier = Modifier.weight(0.8f))
        HashTagTextField(
            hashTags = dummyHashTags.hashTags.toHashSet(),
            onChipUpdated = {_ -> },
            onChipClicked ={_,_ ->} ,
            onChipDeleteClicked = { _, _ ->}
        )
    }
}


//