package com.knu.quickthink.screens.card

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(
): ViewModel(){
    private val _title = MutableStateFlow("")
    val title : StateFlow<String> = _title.asStateFlow()

    private val _content = MutableStateFlow(TextFieldValue(text = ""))
    val content : StateFlow<TextFieldValue> = _content.asStateFlow()

    private val _isContentEditing = MutableStateFlow(false)
    val isContentEditing : StateFlow<Boolean> = _isContentEditing.asStateFlow()

    private val _isPreview = MutableStateFlow(false)
    val isPreview : StateFlow<Boolean> = _isPreview.asStateFlow()

    init {
        fetchContent()
    }

    fun editContent(newContent : TextFieldValue){
        _content.value = newContent
    }

    fun editTitle (newTitle : String){
        _title.value = newTitle
    }

    fun startEditing(){
        Timber.tag("cardEdit").d("startEdit")
        _isContentEditing.value = true
    }

    fun  finishEditing(){
        _isContentEditing.value = false
    }

    fun reverseIsPreview(){
        _isPreview.value = !_isPreview.value
        _isContentEditing.value = !_isPreview.value
    }


    fun updateContent(){

        _isContentEditing.value = false
    }

    fun fetchContent(){
        _title.value = "제목"
        _content.value = _content.value.copy(text = testMarkdown)
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
