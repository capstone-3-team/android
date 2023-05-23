package com.knu.quickthink.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dokar.chiptextfield.*
import com.knu.quickthink.R
import com.knu.quickthink.model.Card
import com.knu.quickthink.screens.main.testCard3

@Composable
fun HashTagTextField(
    card : Card,
    modifier: Modifier = Modifier,
    onChipClicked: (String, Boolean) -> Unit,
    onDeleteButtonClicked: (String, Boolean) -> Unit
) {
    val state = rememberChipTextFieldState(card.hashTags.map { HashTagChip(it) })
    HashTagChipTextField(
        state = state,
        onSubmit = ::HashTagChip,
        modifier = modifier,
        chipStyle = ChipTextFieldDefaults.chipStyle(
            unfocusedBackgroundColor = colorResource(id = R.color.quickThink_blue),
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White
        ),
        onChipClick = { chip ->
            onChipClicked(chip.text,false)
                      },
        chipTrailingIcon = {
            Image(
                imageVector = Icons.Default.Clear,
                contentDescription = null,
                alignment = Alignment.Center,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onDeleteButtonClicked("", false) },
                colorFilter = ColorFilter.tint(Color.White)
            )
        },
        contentPadding = PaddingValues(
            vertical = dimensionResource(id = R.dimen.vertical_margin),
            horizontal = 0.dp
        )
    )
}


@Preview(showBackground = true)
@Composable
fun HashTagTextFieldPrev(){
    HashTagTextField(
        card = testCard3,
        onChipClicked ={_,_ ->} ,
        onDeleteButtonClicked = {_,_ ->}
    )
}


//