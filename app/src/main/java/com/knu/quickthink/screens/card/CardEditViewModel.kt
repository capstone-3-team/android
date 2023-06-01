package com.knu.quickthink.screens.card

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.card.CreateCardRequest
import com.knu.quickthink.model.card.HashTags
import com.knu.quickthink.model.card.UpdateCardRequest
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.emptyMyCard
import com.knu.quickthink.model.onErrorOrException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.card.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


data class CardEditUiState(
    val isLoading : Boolean = false,
    val myCard: MyCard = emptyMyCard,
    val content : TextFieldValue = TextFieldValue(""),
    val isContentEditing : Boolean = false,
    val isPreview : Boolean = false
)

@HiltViewModel
class CardEditViewModel @Inject constructor(
    private val cardRepository: CardRepository
): ViewModel(){

    private val _uiState  = MutableStateFlow(CardEditUiState())
    val uiState: StateFlow<CardEditUiState> = _uiState.asStateFlow()

    fun startEditing(){
        Timber.tag("cardEdit").d("startEdit")
        _uiState.update { it.copy(isContentEditing = true) }
    }

    fun  finishEditing(){
        _uiState.update { it.copy(isContentEditing = false) }
    }

    fun reverseIsPreview(){
        _uiState.update { it.copy(
            isPreview = !it.isPreview,
//            isContentEditing = !it.isContentEditing
            )
        }
    }

    /**
     * 키보드를 닫았을 때 카드 전체 내용을 서버에 업데이트 한다
    * */
    fun updateCard(){
        viewModelScope.launch {
            val card = uiState.value.myCard
            cardRepository.updateCard(
                cardId = uiState.value.myCard.id,
                updateCardRequest = UpdateCardRequest(
                    title = card.title,
                    content = uiState.value.content.text,                       // content는 textFieldValue로 따로 관리하기 때문에 직접 가져와줘야함
                    hashTags = card.hashTags.toList(),
                    writtenDate = card.writtenDate
                )
            ).onErrorOrException { code, message ->
                Timber.e("updateCard onError : code $code , message $message")
            }
        }
        _uiState.update { it.copy(isContentEditing = false) }
    }

    /**
     *  fetchContent  -> 처음 카드 내용 가져오기
     *  */
    fun fetchMyCard(cardId : Long){
        viewModelScope.launch {
            updateUiState(_uiState.value.copy(isLoading = true))
            if (cardId == -1L){
                createMyCard()
            }else{
                cardRepository.fetchMyCard(cardId)
                    .onSuccess {
                        updateUiState(uiState.value.copy(
                            myCard = it.copy(id = cardId),
                            content = uiState.value.content.copy(text = it.content),
                            isLoading = false
                        ))
                    }.onErrorOrException { code, message ->
                        updateUiState(uiState.value.copy(
                            myCard = emptyMyCard,
                            content = uiState.value.content.copy(text ="카드를 불러오지 못했습니다"),
                            isLoading = false
                        ))
                    }
            }
        }
    }

    private suspend fun createMyCard() {
        cardRepository.createCard(CreateCardRequest())
            .onSuccess {
                updateUiState(_uiState.value.copy(
                    myCard = uiState.value.myCard.copy(id = it.cardId),
                    isLoading = false
                ))
            }
            .onErrorOrException{ code: Int, message: String? ->
                Timber.e("updateCard onError : code $code , message $message")
            }
    }

    fun updateUiState(newState : CardEditUiState){
        _uiState.update {newState}
    }

    fun updateUiStateOfCard(card : MyCard){
        _uiState.update { it.copy(myCard = card) }
    }
}
val testMarkdown = """
    # Demo

    Emphasis, aka italics, with *asterisks* or _underscores_. Strong emphasis, aka bold, with **asterisks** or __underscores__. Combined emphasis with **asterisks and _underscores_**. [Links with two blocks, text in square-brackets, destination is in parentheses.](https://www.example.com). Inline `code` has `back-ticks around` it.

    1. First ordered list item
    2. Another item
        * Unordered sub-list.
    3. And another item.
        You can have properly indented paragraphs within list items. Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).

    * Unordered list can use asterisks
    - Or minuses
    + Or pluses
    ---

    ```javascript
    var s = "code blocks use monospace font";
    alert(s);
    ```

    Markdown | Table | Extension
    --- | --- | ---
    *renders* | `beautiful images` | ![random image](https://picsum.photos/seed/picsum/400/400 "Text 1")
    1 | 2 | 3

    > Blockquotes are very handy in email to emulate reply text.
    > This line is part of the same quote.
     # Demo

    Emphasis, aka italics, with *asterisks* or _underscores_. Strong emphasis, aka bold, with **asterisks** or __underscores__. Combined emphasis with **asterisks and _underscores_**. [Links with two blocks, text in square-brackets, destination is in parentheses.](https://www.example.com). Inline `code` has `back-ticks around` it.

    1. First ordered list item
    2. Another item
        * Unordered sub-list.
    3. And another item.
        You can have properly indented paragraphs within list items. Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).

    * Unordered list can use asterisks
    - Or minuses
    + Or pluses
    ---

    ```javascript
    var s = "code blocks use monospace font";
    alert(s);
    ```

    Markdown | Table | Extension
    --- | --- | ---
    *renders* | `beautiful images` | ![random image](https://picsum.photos/seed/picsum/400/400 "Text 1")
    1 | 2 | 3

    > Blockquotes are very handy in email to emulate reply text.
    > This line is part of the same quote.
    """.trimIndent()
