package com.knu.quickthink.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.knu.quickthink.R
import com.knu.quickthink.components.CardDeleteConfirmDialog
import com.knu.quickthink.components.CenterCircularProgressIndicator
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.dummyMyCard
import com.knu.quickthink.screens.card.CardReviewViewModel
import com.knu.quickthink.screens.card.CardStatusRow
import com.knu.quickthink.utils.convertDateFormat

@Composable
fun CardReviewScreen(
    cardId : Long,
    viewModel: CardReviewViewModel = hiltViewModel(),
    onEditBtnClicked : (Long) -> Unit,
    onCloseBtnClicked : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val card = uiState.myCard

    LaunchedEffect(Unit){
        viewModel.fetchMyCard(cardId)
    }
    LaunchedEffect(uiState.isDeleted){
        if(uiState.isDeleted){
            onCloseBtnClicked()
        }
    }
    if(showDeleteConfirmDialog){
        CardDeleteConfirmDialog(
            onDeleteBtnClicked = { viewModel.deleteCard() },
            onCloseBtnClicked = { showDeleteConfirmDialog = false }
        )
    }

    if(uiState.isLoading){
        CenterCircularProgressIndicator()
    }else{
        CardReview(
            card = uiState.myCard,
            onEditClicked = { onEditBtnClicked(cardId) },
            onDeleteClicked = { showDeleteConfirmDialog = true },
            onCloseClicked = onCloseBtnClicked,
            onReviewClicked = {
                viewModel.reviewCard()
                onCloseBtnClicked()
            }
        )
    }

}

@Composable
fun CardReview(
    card : MyCard,
    onEditClicked : () -> Unit,
    onDeleteClicked : () -> Unit,
    onCloseClicked : () -> Unit,
    onReviewClicked : () -> Unit,
) {
    val scrollState = rememberScrollState()
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
        ) {
            Spacer(modifier = Modifier.weight(0.7f))
            IconButton(
                onClick = onEditClicked,
                modifier = Modifier.weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "edit",
                )
            }
            IconButton(
                onClick = onDeleteClicked,
                modifier = Modifier.weight(0.1f)
            ){
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "delete",
                )
            }
            IconButton(
                onClick = onCloseClicked,
                modifier = Modifier.weight(0.1f)
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
        Column(modifier = Modifier
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
                    .fillMaxWidth()
                ,
                mainAxisSpacing = 5.dp,
            ) {
                card.hashTags.forEach { hashTag ->
                    Text(
                        text = "#$hashTag",
                        color = Color.Blue.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }
        }
        CardStatusRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp),
            reviewCount = card.reviewCount,
            writtenDate = convertDateFormat(card.writtenDate),
            latestReviewDate = convertDateFormat(card.latestReviewDate)
        )
        Button(
            onClick = onReviewClicked,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(vertical = dimensionResource(id = R.dimen.vertical_margin)),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = colorResource(id = R.color.quickThink_blue),
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Review",
                style = MaterialTheme.typography.h6,
            )
        }
    }
}
@Preview(showBackground = true,widthDp = 400, heightDp = 600, )
@Composable
fun CardReviewPrev() {
    Surface(modifier = Modifier.padding(10.dp).clip(RoundedCornerShape(20.dp))) {
        CardReview(
            card = dummyMyCard,
            onEditClicked = { /*TODO*/ },
            onDeleteClicked = { /*TODO*/ },
            onCloseClicked = { /*TODO*/ }) {
        }
    }

}
