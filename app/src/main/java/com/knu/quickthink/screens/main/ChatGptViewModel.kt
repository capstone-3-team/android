package com.knu.quickthink.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.card.CreateCardRequest
import com.knu.quickthink.model.chatGPT.ChatGptRequest
import com.knu.quickthink.model.chatGPT.MessageRequest
import com.knu.quickthink.model.onErrorOrException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.card.CardRepository
import com.knu.quickthink.repository.chatGpt.ChatGptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ChatGptUiState(
    val isChatGptTyping : Boolean = false,
    val messageDataList : List<MessageData> = emptyList(),                          // index 0이 가장 최신 메세지
    val savedCardId : Long? = null,
    val showCardEditDialog : Boolean = false,
    val errorMessage : String = ""
)

data class MessageData(
    val messageRequest: MessageRequest,
    val isUserMe : Boolean,
    val isSaved : Boolean  = false
)

@HiltViewModel
class ChatGptViewModel @Inject constructor(
    private val chatGptRepository: ChatGptRepository,
    private val cardRepository: CardRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ChatGptUiState())
    val uiState: StateFlow<ChatGptUiState> = _uiState.asStateFlow()

    fun askToGpt(question : String){
        _uiState.update { it.copy(isChatGptTyping = true) }
        addChatMessage(question,isUserMe = true)
        viewModelScope.launch {
            chatGptRepository.askToGpt(ChatGptRequest(messages = uiState.value.messageDataList.map { it.messageRequest }.asReversed()))
                .onSuccess {
                    Timber.d("응답 : ${it.choices[0].message.content}")
                    addChatMessage(it.choices[0].message,isUserMe = false)
                }.onErrorOrException { code, message ->
                    Timber.e("updateCard onError : code $code , message $message")
                }
            _uiState.update { it.copy(isChatGptTyping = false) }
        }
    }

    fun saveMessage(message: String){
        viewModelScope.launch {
            cardRepository.createCard(CreateCardRequest(content = message))
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(savedCardId = it.cardId, showCardEditDialog = true)
                    }
                }
                .onErrorOrException{ code: Int, message: String? ->
                    Timber.e("updateCard onError : code $code , message $message")
                }
        }
    }

    fun updateUiState(uiState: ChatGptUiState){
        _uiState.update { uiState }
    }

    private fun addChatMessage(message: String, isUserMe: Boolean){
        val updatedChatMessages = uiState.value.messageDataList.toMutableList()
        updatedChatMessages.add(0,MessageData(MessageRequest(content = message),isUserMe = isUserMe))
        _uiState.update { it.copy(messageDataList = updatedChatMessages) }
    }

    private fun addChatMessage(messageRequest: MessageRequest, isUserMe: Boolean){
        val updatedChatMessages = uiState.value.messageDataList.toMutableList()
        updatedChatMessages.add(0,MessageData(messageRequest,isUserMe = isUserMe))
        _uiState.update { it.copy(messageDataList = updatedChatMessages) }
    }

    fun fakeAskToGpt(question: String){
        _uiState.update { it.copy(isChatGptTyping = true) }
        addChatMessage(message = question,isUserMe = true)
        viewModelScope.launch {
            delay(1000L)
            addChatMessage("test Response",false)
            _uiState.update { it.copy(isChatGptTyping = false) }
        }
    }

}
