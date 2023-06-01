package com.knu.quickthink.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.knu.quickthink.R
import com.knu.quickthink.model.card.Card
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.dummyMyCard

@Composable
fun CardViewScreen(
    card : MyCard = dummyMyCard
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
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(0.7f))
            IconButton(
                onClick = {},
                modifier = Modifier.weight(0.1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "edit",
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier.weight(0.1f)
            ){
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "complete",
                )
            }
            IconButton(
                onClick = {},
                modifier = Modifier.weight(0.1f)
            ){
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "complete",
                )
            }
        }
        Text(
            text = card.title,
            style = MaterialTheme.typography.h5,

        )
        Divider(
            color = Color.Black,
            thickness = 2.dp
        )
        Box(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f)
            ,
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
        Button(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.vertical_margin))
        ) {
            Text(
                text = "Edit",
                style = MaterialTheme.typography.h6,
                color = Color.White
            )
        }
    }

}