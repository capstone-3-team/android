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
import com.knu.quickthink.model.Card

val testCard = Card(
    id = "-NTh2lX5KeEMvu3j8zxD",
    title = "title1",
    content = "국가는 과학기술의 혁신과 정보 및 인력의 개발을 통하여 국민경제의 발전에 노력하여야 한다. 모든 국민은 근로의 권리를 가진다. 국가는 사회적·경제적 방법으로 근로자의 고용의 증진과 적정임금의 보장에 노력하여야 하며, 법률이 정하는 바에 의하여 최저임금제를 시행하여야 한다.\n" +
            "대법원장과 대법관이 아닌 법관의 임기는 10년으로 하며, 법률이 정하는 바에 의하여 연임할 수 있다. 대통령은 취임에 즈음하여 다음의 선서를 한다. 정부는 회계연도마다 예산안을 편성하여 회계연도 개시 90일전까지 국회에 제출하고, 국회는 회계연도 개시 30일전까지 이를 의결하여야 한다.\n" +
            "연소자의 근로는 특별한 보호를 받는다. 타인의 범죄행위로 인하여 생명·신체에 대한 피해를 받은 국민은 법률이 정하는 바에 의하여 국가로부터 구조를 받을 수 있다.\n" +
            "국가의 세입·세출의 결산, 국가 및 법률이 정한 단체의 회계검사와 행정기관 및 공무원의 직무에 관한 감찰을 하기 위하여 대통령 소속하에 감사원을 둔다",
    hashTags = listOf("대한민국_헌법","로렘입숨","소프트웨어공학","퀵씽크")
)
@Composable
fun CardViewScreen(
    card : Card = testCard
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