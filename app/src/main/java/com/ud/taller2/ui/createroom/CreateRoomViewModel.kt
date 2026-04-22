package com.ud.taller2.ui.createroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.taller2.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class CreateRoomViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _uiState = MutableStateFlow(CreateRoomUiState())
    val uiState: StateFlow<CreateRoomUiState> = _uiState.asStateFlow()

    fun createRoom(playerName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val playerId = UUID.randomUUID().toString() // Generate it here first
            val result = repository.createRoom(playerName, playerId) // Pass it here

            result.fold(
                onSuccess = { roomCode ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            roomCode = roomCode,
                            playerId = playerId // This now matches the DB
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to create room"
                        )
                    }
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = CreateRoomUiState()
    }
}

data class CreateRoomUiState(
    val isLoading: Boolean = false,
    val roomCode: String = "",
    val playerId: String = "",
    val error: String? = null
)