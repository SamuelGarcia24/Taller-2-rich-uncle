package com.ud.taller2.ui.lobby

import android.util.Log
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
            Log.d("LobbyViewModel", "Loading room: $roomCode")

            repository.listenToRoom(roomCode).collect { room ->
                if (room != null) {
                    Log.d("LobbyViewModel", "Room updated: status=${room.status}, players=${room.players.size}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            room = room,
                            players = room.players.values.toList()
                        )
                    }
                } else {
                    Log.d("LobbyViewModel", "Room is null")
                    _uiState.update { it.copy(isLoading = false, error = "Room not found") }
                }
            }
        }
    }

    fun setPlayerReady(roomCode: String, playerId: String, isReady: Boolean) {
        viewModelScope.launch {
            Log.d("LobbyViewModel", "Setting player $playerId ready: $isReady in room $roomCode")
            try {
                repository.setPlayerReady(roomCode, playerId, isReady)
                Log.d("LobbyViewModel", "Successfully set player ready")
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error setting player ready", e)
                _uiState.update { it.copy(error = "Failed to update ready status") }
            }
        }
    }

    fun startGame(roomCode: String) {
        viewModelScope.launch {
            Log.d("LobbyViewModel", "Starting game in room: $roomCode")
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
