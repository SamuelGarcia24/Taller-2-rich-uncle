package com.ud.taller2.ui.lobby

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.taller2.data.model.PlayerRoom
import com.ud.taller2.data.model.Room
import com.ud.taller2.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LobbyViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _uiState = MutableStateFlow(LobbyUiState())
    val uiState: StateFlow<LobbyUiState> = _uiState.asStateFlow()

    fun loadRoom(roomCode: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.listenToRoom(roomCode).collect { room ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        room = room,
                        players = room?.players?.values?.toList() ?: emptyList()
                    )
                }
            }
        }
    }

    fun setPlayerReady(roomCode: String, playerId: String, isReady: Boolean) {
        viewModelScope.launch {
            repository.setPlayerReady(roomCode, playerId, isReady)
        }
    }

    fun startGame(roomCode: String) {
        viewModelScope.launch {
            // Update room status to playing
            repository.startGame(roomCode)
        }
    }

    fun leaveRoom(roomCode: String, playerId: String) {
        viewModelScope.launch {
            repository.leaveRoom(roomCode, playerId)
        }
    }
}

data class LobbyUiState(
    val isLoading: Boolean = false,
    val room: Room? = null,
    val players: List<PlayerRoom> = emptyList(),
    val error: String? = null
)