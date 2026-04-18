package com.ud.taller2.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    /**
     * Create a new game room
     */
    fun createRoom(playerName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                playerName = playerName
            )

            // TODO: Person 3 - Implement Firebase room creation
            // val roomCode = firebaseRepository.createRoom(playerName)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                roomCreated = true
            )
        }
    }

    /**
     * Join an existing game room
     */
    fun joinRoom(roomCode: String, playerName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                playerName = playerName,
                roomCode = roomCode
            )

            // TODO: Person 3 - Implement Firebase room joining
            // val success = firebaseRepository.joinRoom(roomCode, playerName)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                roomJoined = true
            )
        }
    }

    /**
     * Reset state after navigation
     */
    fun resetState() {
        _uiState.value = HomeUiState()
    }
}

/**
 * UI State for Home Screen
 */
data class HomeUiState(
    val isLoading: Boolean = false,
    val playerName: String = "",
    val roomCode: String = "",
    val roomCreated: Boolean = false,
    val roomJoined: Boolean = false,
    val error: String? = null
)