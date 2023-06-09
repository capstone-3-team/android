package com.knu.quickthink.screens.feed

import android.app.Activity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.knu.quickthink.R
import com.knu.quickthink.components.*
import com.knu.quickthink.model.card.Cards
import com.knu.quickthink.model.card.HashTagItem
import com.knu.quickthink.model.card.HashTags
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.otherscard.OthersCard
import com.knu.quickthink.screens.search.UserInfo
import timber.log.Timber

@Composable
fun OthersFeedScreen(
    othersGoogleId : String,
    viewModel :OthersFeedViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val hashTagListState = rememberLazyListState()

    LaunchedEffect(Unit){
        viewModel.init(othersGoogleId)
    }

    if(uiState.showCardViewDialog){
        if(uiState.selectedCard != null){
            OthersCardViewDialog(
                card = uiState.selectedCard!!,
                onDismissRequest = {viewModel.closeCardViewDialog()}
            )
        }
    }

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
                OthersFeedContent(
                    cards = uiState.cards,
                    onCardClick = {viewModel.cardSelect(it) }
                )
            }
        }
        UserInfoBox(user = uiState.othersUserInfo)
    }
}

@Composable
fun UserInfoBox(user: UserInfo) {
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
            profileImage(
                imageUrl = user.profilePicture,
                imageSize = 16.dp,
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(user.googleName)
        }
    }
}

@Composable
fun OthersFeedContent(
    modifier: Modifier = Modifier,
    cards: Cards<OthersCard>,
    onCardClick: (Long) -> Unit,
) {
    LazyColumn {
        itemsIndexed(cards.cards) { index, card ->
            OthersCardItem(
                card = card,
                onCardClick = onCardClick,
            )
        }
    }
}

@Composable
fun OthersCardItem(
    card: OthersCard,
    onCardClick: (Long) -> Unit,
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
        OthersCardItemHeader(card = card)
        Divider( color = Color.Black, thickness = 2.dp)
        CardBox(content = card.content)
        hashTagFlowRow(hashTags = card.hashTags)
    }
}

@Composable
fun OthersCardViewDialog(
    card : OthersCard,
    onDismissRequest : () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ){
        val scrollState = rememberScrollState()
        Surface(
            modifier = Modifier
                .width(400.dp)
                .height(600.dp)
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                        vertical = dimensionResource(id = R.dimen.list_item_padding),
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onDismissRequest,
                        interactionSource = MutableInteractionSource()
                    ){
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "close",
                        )
                    }
                }
                Text(
                    text = card.title,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Divider(
                    color = Color.Black,
                    thickness = 2.dp
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = dimensionResource(id = R.dimen.vertical_margin))
                ) {
                    RichText(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Markdown(content = card.content)
                    }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        mainAxisSpacing = 5.dp,
                    ) {
                        card.hashTags.forEach { hashTag ->
                            Text(
                                text = "#$hashTag",
                                color = colorResource(id = R.color.quickThink_blue),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
