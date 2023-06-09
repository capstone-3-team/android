package com.knu.quickthink.screens.main

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.knu.quickthink.R
import com.knu.quickthink.components.CardFeedContent
import com.knu.quickthink.components.CenterCircularProgressIndicator
import com.knu.quickthink.model.card.HashTagItem
import com.knu.quickthink.screens.feed.FeedViewModel
import timber.log.Timber

@Composable
fun FeedScreen(
    viewModel :FeedViewModel = hiltViewModel(),
    onCardClick : (Long) -> Unit,
    onCardEditClick : (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val hashTagListState = rememberLazyListState()
    val window = (LocalContext.current as? Activity)?.window

    if (window != null) {
        WindowCompat.setDecorFitsSystemWindows(window, true)        // CardEdit 갔다가 돌아왔을 때 부드럽게 연결하기 위해 DisposableEffect대신 사용
    }

    Timber.d("cards : ${uiState.cards}")
    Column(modifier = Modifier.fillMaxSize()){
        HashTagFilteringBar(
            hashTags = uiState.hashTags.map { HashTagItem(it.key,it.value) },
            lazyListState = hashTagListState,
            onHashTagClicked = {viewModel.hashTagSelect(it)},
            onFilterBtnClicked = {}
        )
        if(uiState.isLoading){
            CenterCircularProgressIndicator()
        }else {
            CardFeedContent(
                cards = uiState.cards,
                currentFilteringLabel = R.string.app_name,
                onCardClick = onCardClick,
                onCardEditClick = onCardEditClick,
                onCardReviewed = {
                    viewModel.reviewCard(it)
                }
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
            state = lazyListState
        ){
            itemsIndexed(hashTags){ index, hashTag->
                hashTagChip(
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
    hashTag: String,
    isSelected : Boolean,
    onHashTagClicked : (String) -> Unit
) {
    val chipColor = colorResource(id = R.color.quickThink_blue)
    Chip(
        onClick = {onHashTagClicked(hashTag)},
        modifier = Modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.chip_roundedCorner)),
        colors = ChipDefaults.chipColors(
            backgroundColor = if(isSelected) chipColor else chipColor.copy(0.3f)
        )
    ) {
       Text(text = "#$hashTag")
    }
}

@Preview
@Composable
fun HashTagFilterBarPrev() {
    Surface() {
        val hashTagListState = rememberLazyListState()
        HashTagFilteringBar(
            hashTags = listOf(HashTagItem("테스트",false))+ List(20){HashTagItem("테스트$it",true)},
            hashTagListState,
            onHashTagClicked = {},
            onFilterBtnClicked = {}
        )
    }

}