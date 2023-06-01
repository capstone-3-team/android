package com.knu.quickthink.screens.card

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.dummyMyCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject


data class CardEditUiState(
    val myCard: MyCard = dummyMyCard,
    val content : TextFieldValue = TextFieldValue(""),
    val isContentEditing : Boolean = false,
    val isPreview : Boolean = false
)

@HiltViewModel
class CardViewModel @Inject constructor(
): ViewModel(){

    private val _uiState  = MutableStateFlow(CardEditUiState())
    val uiState: StateFlow<CardEditUiState> = _uiState.asStateFlow()

    init {
        fetchContent()
    }

    fun editContent(newContent : TextFieldValue){
        _uiState.update { it.copy(content = newContent) }
    }

    fun editTitle (newTitle : String){
        _uiState.update { it.copy(myCard = it.myCard.copy(title = newTitle)) }
    }

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
            isContentEditing = !it.isContentEditing)
        }
    }


    fun updateContent(){
        _uiState.update { it.copy(isContentEditing = false) }
    }

    fun fetchContent(){


        _uiState.update { state ->
            state.copy(
                myCard = state.myCard.copy(title =  "제목"),
                content = state.content.copy(text = testMarkdown)
            )

        }
    }
    /* TODO : fetchContent  -> 처음 카드 내용 가져오기*/

    /* TODO: updateContent -> 카드 내용 저장*/
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
