package com.knu.quickthink.model.chatGPT

import com.knu.quickthink.screens.main.MessageData

data class MessageRequest(
    val role : String = "user",
    val content : String = ""
)

val dummyMessageDataList = List(20){
    MessageData(
        messageRequest = MessageRequest(content = "테스트${20-it} 입력입니다ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ"),
        isUserMe = (20-it) %2 == 0
    )
}+ listOf(MessageData(messageRequest = MessageRequest(content =
"OPENAI의 목적은 인공지능 연구의 발전을 촉진하고 인공지능이 인류의 복지에 긍정적인 영향을 미칠 수 있도록 하는 것입니다.\n\n "+
        "OPENAI는 혁신적인 기술과 인공지능 연구에 대한 지식을 공유하고 협업하는 방식으로 세계적인 인공지능 전문가들을 모으고 있습니다.\n" +"\n" +
        "이를 통해 OPENAI는 인공지능 연구 및 개발 분야에서 지속적인 진보와 혁신을 이루어내고, 사람들의 일상 생활에 유용한 인공지능 기술을 제공하는 것을 목표로 하고 있습니다."
),isUserMe = false))
