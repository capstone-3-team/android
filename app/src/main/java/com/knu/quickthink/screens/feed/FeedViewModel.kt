package com.knu.quickthink.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.card.Card
import com.knu.quickthink.model.card.Cards
import com.knu.quickthink.model.card.HashTags
import com.knu.quickthink.model.card.emptyMyCards
import com.knu.quickthink.model.card.mycard.MyCard
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

data class FeedUiState(
    val isLoading : Boolean = false,
    val cards : Cards<MyCard> = emptyMyCards,
    val hashTags: HashTags = HashTags(emptyList()),
    val message : String = ""
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val cardRepository: CardRepository,
): ViewModel(){

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState :StateFlow<FeedUiState> = _uiState.asStateFlow()

    init {
        fetchMyCards()
    }

    private fun fetchMyCards(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cardRepository.fetchMyCards(_uiState.value.hashTags)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(cards = it, isLoading = false)
                    }
                }.onErrorOrException{ code, message ->
                    Timber.d("code : $code  message : $message")
                    _uiState.update { state ->
                        state.copy(cards = emptyMyCards, isLoading = false, message = message ?: "알 수 없는 오류")
                    }
                }
        }
    }

    fun reviewCard(cardId : Long) {

    }
}