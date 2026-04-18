package com.ud.taller2.ui.joinroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.taller2.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class JoinRoomViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _uiState = MutableStateFlow(JoinRoomUiState())
    val uiState: StateFlow<JoinRoomUiState> = _uiState.asStateFlow()

    fun joinRoom(roomCode: String, playerName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Validate room code format
            if (roomCode.length != 6) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Room code must be 6 characters"
                    )
                }
                return@launch
            }

            val result = repository.joinRoom(roomCode, playerName)

            result.fold(
                onSuccess = { playerId ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isJoined = true,
                            roomCode = roomCode,
                            playerId = playerId
                        )
                    }
                },
                onFailure = { error ->
                    val errorMessage = when {
                        error.message?.contains("Room not found") == true ->
                            "Room not found. Check the code and try again."
                        error.message?.contains("Game already started") == true ->
                            "This game has already started."
                        error.message?.contains("Room is full") == true ->
                            "This room is full (max 3 players)."
                        else -> error.message ?: "Failed to join room"
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = JoinRoomUiState()
    }
}

data class JoinRoomUiState(
    val isLoading: Boolean = false,
    val isJoined: Boolean = false,
    val roomCode: String = "",
    val playerId: String = "",
    val error: String? = null
)