package com.knu.quickthink.screens.main

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.knu.quickthink.R
import com.knu.quickthink.components.CardFeedContent
import com.knu.quickthink.model.card.Card
import com.knu.quickthink.screens.feed.FeedViewModel
import timber.log.Timber

@Composable
fun FeedScreen(
    viewModel :FeedViewModel = hiltViewModel(),
    onCardClick : (Long) -> Unit,
    onCardEditClick : (Long) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()
    val window = (LocalContext.current as? Activity)?.window

    if (window != null) {
        WindowCompat.setDecorFitsSystemWindows(window, true)        // CardEdit 갔다가 돌아왔을 때 부드럽게 연결하기 위해 DisposableEffect대신 사용
    }

    Timber.d("cards : ${uiState.cards}")

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