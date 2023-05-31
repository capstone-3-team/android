package com.knu.quickthink.utils

import androidx.compose.ui.text.input.TextFieldValue

private fun cursorPositionToPixelOffset(
    textFieldValue: TextFieldValue,
    cursorPosition: Int,
    lineHeightPx: Float,
    totalLineCount: Int
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
    return (offset / totalLineCount * lineHeightPx).toInt()
//    return (offset / lines.size.toFloat() * lineHeightPx).toInt()
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun HashTagInputChip(
//    text : String,
//    selected: Boolean,
//    modifier: Modifier = Modifier,
//    onChipClicked: (String, Boolean) -> Unit,
//    onDeleteButtonClicked: (String, Boolean) -> Unit
//){
//    InputChip(
//        label = {
//            Text(
//                text = text,
//                style = MaterialTheme.typography.button,
//            )
//        },
//        selected = selected,
//        onClick = {onChipClicked("",false)},
//        colors= InputChipDefaults.inputChipColors(
//            containerColor = colorResource(id = R.color.quickThink_blue)
//        ),
//        trailingIcon = {
//            Image(
//                imageVector = Icons.Default.Clear,
//                contentDescription = null,
//                alignment = Alignment.Center,
//                modifier = Modifier
//                    .clickable { onDeleteButtonClicked(text, selected) },
//                colorFilter = ColorFilter.tint(Color.White)
//            )
//        }
//    )
//}
//
//
//@Composable
//fun HashTagChip(
//    text: String,
//    selected: Boolean,
//    modifier: Modifier = Modifier,
//    onChipClicked: (String, Boolean) -> Unit,
//    onDeleteButtonClicked: (String, Boolean) -> Unit
//    ) {
//        Surface(
//            color = when {
//                selected -> colorResource(id = R.color.quickThink_blue)
//                else -> colorResource(id = R.color.quickThink_blue)
//            },
//            contentColor = Color.White,
//            shape = RoundedCornerShape(dimensionResource(id = R.dimen.chip_roundedCorner)),
//            border = BorderStroke(
//                width = 1.dp,
//                color = when {
//                    selected -> colorResource(id = R.color.quickThink_blue)
//                    else -> colorResource(id = R.color.quickThink_blue)
//                }
//            ),
//            modifier = modifier
//        ) {
//            Row(
//                modifier = Modifier
//                    .padding(vertical = dimensionResource(id = R.dimen.vertical_margin))
//                    .clickable { onChipClicked(text, selected) }
//                , verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    text = text,
//                    style = MaterialTheme.typography.button,
//                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
//                )
//                Image(
//                    imageVector = Icons.Default.Clear,
//                    contentDescription = null,
//                    alignment = Alignment.Center,
//                    modifier = Modifier
//                        .padding(
//                            end = dimensionResource(id = R.dimen.horizontal_margin)
//                        )
//                        .size(16.dp)
//                        .clickable { onDeleteButtonClicked(text, selected) },
//                    colorFilter = ColorFilter.tint(Color.White)
//                )
//            }
//        }
//}
//
//@Composable
//fun HashTagFlowRow(
//    modifier: Modifier = Modifier,
//    card : Card
//) {
//    FlowRow(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(dimensionResource(id = R.dimen.vertical_margin)),
//        mainAxisSpacing = 5.dp,
//        crossAxisSpacing = 5.dp
//    ) {
//        card.hashTags.forEach { hashTag ->
//            HashTagInputChip(
//                text = hashTag,
//                selected = true,
//                onChipClicked = { _, _ -> },
//                onDeleteButtonClicked = { _, _ -> }
//            )
////                    Text(
////                        text = "#$hashTag",
////                        color = Color.Blue.copy(alpha = 0.8f),
////                        fontSize = 18.sp
////                    )
//        }
//    }
//}
////
//}
//
////@Preview(showBackground = true)
//@Composable
//fun HashTagInputChipPrev() {
//    HashTagInputChip(
//        text = "대한민국_헌법",
//        selected = false,
//        onChipClicked ={_,_ ->} ,
//        onDeleteButtonClicked = {_,_ ->})
//}
//
////@Preview
//@Composable
//fun HashTagChipPrev() {
//    HashTagChip(
//        text = "대한민국_헌법",
//        selected = false,
//        onChipClicked ={_,_ ->} ,
//        onDeleteButtonClicked = {_,_ ->})
//}
//
//
////@Preview
//@Composable
//fun HashTagFlowRowPrev() {
//    Surface() {
//        HashTagFlowRow(card = testCard3)
//    }
//
//}

//data class ChipState(
//    var text: String,
//    val isSelected: MutableState<Boolean>
//)