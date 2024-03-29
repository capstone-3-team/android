package com.knu.quickthink.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.card.*
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class FeedUiState(
    val isLoading : Boolean = false,
    val cards : Cards<MyCard> = emptyMyCards,
    val hashTags: HashMap<String, Boolean> = HashMap(),
    val message : String = ""
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val cardRepository: CardRepository,
): ViewModel(){

    private val _uiState = MutableStateFlow(FeedUiState(isLoading = true))
    val uiState :StateFlow<FeedUiState> = _uiState.asStateFlow()

    fun fetchContent(){
        fetchHashTags()
        fetchMyCards()
    }
    private fun fetchHashTags() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cardRepository.fetchHashTags(null)
                .onSuccess {hashTags ->
                    _uiState.update { state ->
                        state.copy(hashTags = HashMap<String, Boolean>().apply {
                            hashTags.hashTags.forEach { put(it,false)}
                        })
                    }
                }
                .onErrorOrException { code, message ->
                    Timber.d("code : $code  message : $message")
                }
        }
    }

    private fun fetchMyCards(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cardRepository.fetchMyCards(
                hashTags = HashTags(_uiState.value.hashTags.filterValues { it }.keys.toList())
            ).onSuccess {
                _uiState.update { state ->
                    state.copy(cards = sortCardsByLatestReviewDate(it))
                }
            }.onErrorOrException{ code, message ->
                Timber.d("code : $code  message : $message")
                _uiState.update { state ->
                    state.copy(cards = emptyMyCards, isLoading = false, message = message ?: "알 수 없는 오류")
                }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun hashTagSelect(hashTag : String){
        val updateHashTags = HashMap(uiState.value.hashTags)
        updateHashTags[hashTag] = !updateHashTags[hashTag]!!
        _uiState.update { state ->
            state.copy(hashTags = updateHashTags)
        }
        fetchMyCards()
    }
    fun reviewCard(cardId : Long) {
        viewModelScope.launch {
            cardRepository.reviewCard(cardId)
                .onSuccess {
                    fetchMyCards()
                }.onErrorOrException { code, message ->
                    Timber.e("updateCard onError : code $code , message $message")
                }
        }

    }

    private fun sortCardsByLatestReviewDate(cards: Cards<MyCard>): Cards<MyCard> {
        val updatedCards =  cards.cards.toMutableList().sortedBy{ card ->
            LocalDateTime.parse(card.latestReviewDate, DateTimeFormatter.ISO_DATE_TIME)
        }
//        .forEach {
//            Timber.d("updatedCard  : id: ${it.id} 수정일:${it.latestReviewDate}")
//        }
        return cards.copy(cards = updatedCards)
    }
}