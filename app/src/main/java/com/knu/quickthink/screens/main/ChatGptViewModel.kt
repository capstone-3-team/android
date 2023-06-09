package com.knu.quickthink.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.chatGPT.ChatGptRequest
import com.knu.quickthink.model.chatGPT.ChatMessage
import com.knu.quickthink.model.onErrorOrException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.chatGpt.ChatGptRepository
import com.knu.quickthink.repository.user.UserRepository
import com.knu.quickthink.screens.search.UserSearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ChatGptUiState(
    val isLoading : Boolean = false,
    val chatMessages : List<ChatMessage> = emptyList(),
    val message : String = ""
)

@HiltViewModel
class ChatGptViewModel @Inject constructor(
    private val chatGptRepository: ChatGptRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ChatGptUiState())
    val uiState: StateFlow<ChatGptUiState> = _uiState.asStateFlow()

    fun askToGpt(question : String){
        val updatedChatMessages = uiState.value.chatMessages.toMutableList()
        updatedChatMessages.add(ChatMessage(content = question))
        _uiState.update { state ->
            state.copy(isLoading = true, chatMessages = updatedChatMessages)
        }
        viewModelScope.launch {
            chatGptRepository.askToGpt(ChatGptRequest(messages = uiState.value.chatMessages))
                .onSuccess {
                    Timber.d("응답 : ${it.choices[0].message.content}")
                }.onErrorOrException { code, message ->
                    Timber.e("updateCard onError : code $code , message $message")
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

}
