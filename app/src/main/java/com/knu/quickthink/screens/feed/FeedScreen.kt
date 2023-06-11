package com.knu.quickthink.screens.main

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.knu.quickthink.FeedScreenState
import com.knu.quickthink.R
import com.knu.quickthink.components.*
import com.knu.quickthink.model.card.Cards
import com.knu.quickthink.model.card.HashTagItem
import com.knu.quickthink.model.card.dummyMyCards
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.dummyMyCard
import com.knu.quickthink.screens.feed.FeedViewModel
import timber.log.Timber

@Composable
fun FeedScreen(
    viewModel :FeedViewModel = hiltViewModel(),
    feedScreenState: FeedScreenState,
    onCardClick : (Long) -> Unit,
    onCardEditClick : (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val hashTagListState = feedScreenState.hashTagListState
    val feedListState = feedScreenState.feedListState
    val window = (LocalContext.current as? Activity)?.window

    if (window != null) {
        WindowCompat.setDecorFitsSystemWindows(window, true)        // CardEdit 갔다가 돌아왔을 때 부드럽게 연결하기 위해 DisposableEffect대신 사용
    }

    LaunchedEffect(Unit ){
        viewModel.fetchContent()
    }

    Timber.d("cards : ${uiState.cards}")
    Column(modifier = Modifier.fillMaxSize()){
        HashTagFilteringBar(
            hashTags = uiState.hashTags.map { HashTagItem(it.key,it.value) },
            lazyListState = hashTagListState,
            onHashTagClicked = {viewModel.hashTagSelect(it)},
            onFilterBtnClicked = {}
        )
        Crossfade(
            targetState = uiState.isLoading,
            modifier = Modifier.weight(1f)
        ) { isLoading ->
            if(isLoading){
                CenterCircularProgressIndicator()
            }else if (uiState.cards.cards.isEmpty()){
                NoCardScreen()
            }else {
                CardFeedContent(
                    cards = uiState.cards,
                    lazyListState = feedListState,
                    onCardClick = onCardClick,
                    onCardEditClick = onCardEditClick,
                    onCardReviewed = {
                        viewModel.reviewCard(it)
                    }
                )
            }
        }
    }
}

@Composable
fun CardFeedContent(
//    loading: Boolean,
    cards: Cards<MyCard>,
    lazyListState : LazyListState,
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
        LazyColumn(
            state = lazyListState
        ) {
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
    onCardClick: (Long) -> Unit,
    onCardReviewed: (Long) -> Unit,
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
        CardItemHeader(
            card = card,
            onCardReviewed = onCardReviewed,
            onCardEditClick = onCardEditClick
        )
        Divider(color = Color.Black,thickness = 2.dp)
        CardBox(content = card.content)
        hashTagFlowRow(card.hashTags)
    }
}
@Preview
@Composable
fun CardContentPreview() {
    Surface {
        CardFeedContent(
            cards = dummyMyCards,
            lazyListState = rememberLazyListState(),
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