package com.knu.quickthink.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knu.quickthink.model.card.Cards
import com.knu.quickthink.model.card.HashTags
import com.knu.quickthink.model.card.otherscard.OthersCard
import com.knu.quickthink.model.card.otherscard.emptyOthersCards
import com.knu.quickthink.model.onErrorOrException
import com.knu.quickthink.model.onSuccess
import com.knu.quickthink.repository.card.CardRepository
import com.knu.quickthink.repository.user.UserRepository
import com.knu.quickthink.screens.search.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class OthersFeedUiState(
    val isLoading : Boolean = false,
    val othersUserInfo : UserInfo = UserInfo("","","",null),
    val cards : Cards<OthersCard> = emptyOthersCards,
    val hashTags: HashMap<String, Boolean> = HashMap(),
    val message : String = "",
    val selectedCard : OthersCard? = null,
    val showCardViewDialog : Boolean = false,
)

@HiltViewModel
class OthersFeedViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository
): ViewModel(){

    private val _uiState = MutableStateFlow(OthersFeedUiState())
    val uiState : StateFlow<OthersFeedUiState> = _uiState.asStateFlow()

    fun fetchUserInfo(googleId: String){
        _uiState.update { it.copy(othersUserInfo = it.othersUserInfo.copy(googleId = googleId)) }
        viewModelScope.launch {
            userRepository.fetchUserInfo(googleId)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(othersUserInfo = it) }
                }.onErrorOrException { code, message ->
                    Timber.d("code : $code  message : $message")
                }
        }
    }
    fun fetchHashTags() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cardRepository.fetchHashTags(uiState.value.othersUserInfo.googleId)
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

    fun fetchOthersCards(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            cardRepository.fetchOthersCards(
                googleId = uiState.value.othersUserInfo.googleId,
                hashTags = HashTags(_uiState.value.hashTags.filterValues { it }.keys.toList())
            ).onSuccess {
                _uiState.update { state ->
                    state.copy(cards = it, isLoading = false)
                }
            }.onErrorOrException{ code, message ->
                Timber.d("code : $code  message : $message")
                _uiState.update { state ->
                    state.copy(cards = emptyOthersCards, isLoading = false, message = message ?: "알 수 없는 오류")
                }
            }
        }
    }

    fun hashTagSelect(hashTag : String){
        val updateHashTags = HashMap(uiState.value.hashTags)
        updateHashTags[hashTag] = !updateHashTags[hashTag]!!
        _uiState.update { state ->
            state.copy(hashTags = updateHashTags)
        }
        fetchOthersCards()
    }

    fun init(googleId : String){
        _uiState.update { it.copy(isLoading = true) }
        fetchUserInfo(googleId)
        fetchHashTags()
        fetchOthersCards()
    }

    fun cardSelect(cardId : Long){
        _uiState.update {state ->
            state.copy(
                selectedCard = state.cards.cards.filter { it.id == cardId }[0],
                showCardViewDialog = true
            )
        }
    }

    fun closeCardViewDialog(){
        _uiState.update { it.copy(showCardViewDialog = false) }
    }
}