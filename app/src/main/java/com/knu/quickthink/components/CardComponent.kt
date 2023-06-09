package com.knu.quickthink.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.knu.quickthink.R
import com.knu.quickthink.model.card.Card
import com.knu.quickthink.model.card.Cards
import com.knu.quickthink.model.card.HashTagItem
import com.knu.quickthink.model.card.dummyMyCards
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.dummyMyCard
import com.knu.quickthink.model.card.otherscard.OthersCard
@Composable
fun CardItemHeader(
    card: MyCard,
    onCardReviewed: (Long) -> Unit,
    onCardEditClick: (Long) -> Unit
) {
    Row(
        modifier = Modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = card.title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.horizontal_margin)
                )
                .weight(0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        IconButton(
            onClick = {onCardEditClick(card.id)},
            modifier = Modifier.weight(0.1f)
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "edit",
            )
        }
        IconButton( modifier = Modifier
            .weight(0.1f)
            .padding(end = dimensionResource(id = R.dimen.horizontal_margin)),
            onClick = { onCardReviewed(card.id)}
        ){
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "review button",
            )
        }
    }
}

@Composable
fun OthersCardItemHeader(
    card : OthersCard
) {
    Row(
        modifier = Modifier.height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = card.title,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.horizontal_margin)
                ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Composable
fun CardBox(
    content : String
) {
    Box(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding)
            )
            .heightIn(max = 200.dp)
        ,
        contentAlignment = Alignment.TopStart
    ) {
        RichText() {
            Markdown(content = content)
        }
    }
}
@Composable
fun hashTagFlowRow(
    hashTags : HashSet<String>
) {
    FlowRow(
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding)
            )
            .fillMaxWidth(),
        mainAxisSpacing = 5.dp,
    ) {
        hashTags.forEach { hashTag ->
            Text(
                text = "#$hashTag",
                color = colorResource(id = R.color.quickThink_blue),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun HashTagFilteringBar(
    hashTags: List<HashTagItem>,
    lazyListState : LazyListState,
    onHashTagClicked: (String) -> Unit,
    onFilterBtnClicked : () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.vertical_margin)),
    ){
        LazyRow(
            modifier = Modifier.weight(1f),
            state = lazyListState,
        ){
            itemsIndexed(hashTags){ index, hashTag->
                hashTagChip(
                    modifier = Modifier.padding(
                        start = if(hashTag == hashTags.first()) 0.dp else 4.dp,
                        end = if(hashTag == hashTags.last()) 0.dp else 4.dp
                    ),
                    hashTag = hashTag.value,
                    isSelected = hashTag.isSelected,
                    onHashTagClicked = onHashTagClicked
                )
            }
        }
//        IconButton( modifier = Modifier,
//            onClick = onFilterBtnClicked
//        ){
//            Icon(
//                painter = painterResource(id = R.drawable.baseline_filter_list_24),
//                contentDescription = "filter",
//                modifier = Modifier.size(24.dp)
//            )
//        }
    }


}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun hashTagChip(
    modifier : Modifier = Modifier,
    hashTag: String,
    isSelected : Boolean,
    onHashTagClicked : (String) -> Unit
) {
    val chipColor = colorResource(id = R.color.quickThink_blue)
    Chip(
        modifier = modifier,
        onClick = {onHashTagClicked(hashTag)},
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.chip_roundedCorner)),
        colors = ChipDefaults.chipColors(
            backgroundColor = if(isSelected) chipColor else chipColor.copy(0.3f)
        )
    ) {
        Text(text = "#$hashTag")
    }
}

@Composable
fun NoCardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "해당하는 카드가 없습니다", style = MaterialTheme.typography.h5)
    }
}

@Preview
@Composable
fun HashTagFilterBarPrev() {
    Surface() {
        val hashTagListState = rememberLazyListState()
        HashTagFilteringBar(
            hashTags = listOf(HashTagItem("테스트",false))+ List(20){ HashTagItem("테스트$it",true) },
            hashTagListState,
            onHashTagClicked = {},
            onFilterBtnClicked = {}
        )
    }
}