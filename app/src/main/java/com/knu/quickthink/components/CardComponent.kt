package com.knu.quickthink.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.knu.quickthink.R
import com.knu.quickthink.model.card.Card
import com.knu.quickthink.model.card.Cards
import com.knu.quickthink.model.card.dummyMyCards
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.dummyMyCard


@Composable
fun CardFeedContent(
//    loading: Boolean,
    cards: Cards<MyCard>,
    @StringRes currentFilteringLabel: Int,
    onCardClick: (Long) -> Unit,
    onCardEditClick : (Long) -> Unit,
    onCardReviewed: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
//            .padding(horizontal = dimensionResource(id = R.dimen.horizontal_margin))
    ) {
//        Text(
//            text = stringResource(currentFilteringLabel),
//            modifier = Modifier.padding(
//                horizontal = dimensionResource(id = R.dimen.list_item_padding),
//                vertical = dimensionResource(id = R.dimen.vertical_margin)
//            ),
//            style = MaterialTheme.typography.h6
//        )
        LazyColumn {
            itemsIndexed(cards.cards) { index, card ->
                CardItem(
                    card = card,
                    onCardClick = onCardClick,
                    onCardEditClick = onCardEditClick,
                    onCardReviewed = onCardReviewed
                )
            }
        }
    }
}


@Composable
fun CardItem(
    card: MyCard,
    onCardReviewed: (Long) -> Unit,
    onCardClick: (Long) -> Unit,
    onCardEditClick: (Long) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
            .clip(RoundedCornerShape(10.dp))
            .border(
                border = BorderStroke(2.dp, SolidColor(Color.Black)),
                shape = RoundedCornerShape(10.dp)
            )
            .fillMaxWidth()
            .clickable { onCardClick(card.id) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = card.title,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .padding(
                        start = dimensionResource(id = R.dimen.horizontal_margin)
                    )
                    .weight(0.7f)
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
        Divider(
            color = Color.Black,
            thickness = 2.dp
        )
        Box(
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding)
            ),
            contentAlignment = Alignment.TopStart
        ) {
            Text(
                text = card.content,
                maxLines = 10,
                overflow = TextOverflow.Ellipsis
            )

        }
        FlowRow(
            modifier = Modifier
                .padding(
                    horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                    vertical = dimensionResource(id = R.dimen.list_item_padding)
                )
                .fillMaxWidth(),
            mainAxisSpacing = 5.dp,
        ) {
            card.hashTags.forEach { hashTag ->
                Text(
                    text = "#$hashTag",
                    color = Color.Blue.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun CardContentPreview() {
    Surface {
        CardFeedContent(
            cards = dummyMyCards,
            currentFilteringLabel = R.string.app_name,
            onCardClick = {},
            onCardEditClick = {},
            onCardReviewed = { _ -> }
        )
    }
}


@Preview
@Composable
fun CardItemPreview() {
    Surface {
        CardItem(
            card = dummyMyCard,
            onCardEditClick = {},
            onCardClick = {},
            onCardReviewed = {}
        )
    }

}