package com.knu.quickthink.model.card.mycard

import com.knu.quickthink.model.card.Card
import java.time.LocalDateTime

data class MyCard(
    val id : Long,
    val title : String,
    val content : String,
    val isYours : Boolean,
    val hashTags : HashSet<String>,
    val writtenDate : String,
    val latestReviewDate : String,
    val reviewCount : Long
)

val emptyMyCard = MyCard(
    id = 0,
    title = "제목",
    isYours = true,
    content = "내용을 입력해주세요",
    hashTags = emptyList<String>().toHashSet(),
    writtenDate = "",
    latestReviewDate = "",
    reviewCount = 0
)

val dummyMyCard = MyCard(
    id = 123,
    title = "title1",
    isYours = true,
    content = "국가는 과학기술의 혁신과 정보 및 인력의 개발을 통하여 국민경제의 발전에 노력하여야 한다. 모든 국민은 근로의 권리를 가진다. 국가는 사회적·경제적 방법으로 근로자의 고용의 증진과 적정임금의 보장에 노력하여야 하며, 법률이 정하는 바에 의하여 최저임금제를 시행하여야 한다.\n" +
            "대법원장과 대법관이 아닌 법관의 임기는 10년으로 하며, 법률이 정하는 바에 의하여 연임할 수 있다. 대통령은 취임에 즈음하여 다음의 선서를 한다. 정부는 회계연도마다 예산안을 편성하여 회계연도 개시 90일전까지 국회에 제출하고, 국회는 회계연도 개시 30일전까지 이를 의결하여야 한다.\n" +
            "연소자의 근로는 특별한 보호를 받는다. 타인의 범죄행위로 인하여 생명·신체에 대한 피해를 받은 국민은 법률이 정하는 바에 의하여 국가로부터 구조를 받을 수 있다.\n" +
            "국가의 세입·세출의 결산, 국가 및 법률이 정한 단체의 회계검사와 행정기관 및 공무원의 직무에 관한 감찰을 하기 위하여 대통령 소속하에 감사원을 둔다",
    hashTags = listOf("대한민국_헌법","로렘입숨","소프트웨어공학","퀵씽크").toHashSet(),
    writtenDate = "2023-05-31T14:41:47.733",
    latestReviewDate = "2023-05-31T14:41:47.733",
    reviewCount = 3
)