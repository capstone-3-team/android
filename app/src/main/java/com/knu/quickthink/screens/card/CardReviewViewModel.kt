package com.knu.quickthink.screens.card

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.card.mycard.MyCard
import com.knu.quickthink.model.card.mycard.dummyMyCard
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

data class CardReviewUiState(
    val isLoading : Boolean = true,
    val myCard: MyCard = emptyMyCard,
    val isDeleted : Boolean = false
)

@HiltViewModel
class CardReviewViewModel @Inject constructor(
    private val cardRepository: CardRepository
): ViewModel(){

    private val _uiState  = MutableStateFlow(CardReviewUiState())
    val uiState: StateFlow<CardReviewUiState> = _uiState.asStateFlow()

    fun fetchMyCard(cardId : Long){
        viewModelScope.launch {
            uiStateUpdate(_uiState.value.copy(isLoading = true))
            cardRepository.fetchMyCard(cardId)
                .onSuccess {
                    uiStateUpdate(uiState.value.copy(
                        myCard = it.copy(id = cardId),
                        isLoading = false
                    ))
                }.onErrorOrException { code, message ->
                    uiStateUpdate(uiState.value.copy(
                        myCard = emptyMyCard.copy(content = "카드를 불러오지 못했습니다"),
                        isLoading = false
                    ))
                }
        }
    }
    fun deleteCard(){
        viewModelScope.launch {
            cardRepository.deleteCard(_uiState.value.myCard.id)
                .onSuccess {
                    _uiState.update { it.copy(isDeleted = true) }
                }
                .onErrorOrException { code, message ->
                    Timber.e("updateCard onError : code $code , message $message")
                }
        }
    }

    fun reviewCard(){
        viewModelScope.launch {
            cardRepository.reviewCard(_uiState.value.myCard.id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(myCard = state.myCard.copy(reviewCount = state.myCard.reviewCount+1)) }
                }.onErrorOrException { code, message ->
                    Timber.e("updateCard onError : code $code , message $message")
                }
        }
    }

    private fun uiStateUpdate(newState : CardReviewUiState){
        _uiState.update {newState}
    }
}
