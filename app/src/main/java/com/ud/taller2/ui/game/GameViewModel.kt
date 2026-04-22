package com.ud.taller2.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ud.taller2.data.model.getRandomEvents
import com.ud.taller2.data.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random


class GameViewModel : ViewModel() {

    private val repository = FirebaseRepository()
    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    private var currentRoomCode: String = ""
    private var currentPlayerId: String = ""

    fun initGame(roomCode: String, playerId: String) {
        currentRoomCode = roomCode
        currentPlayerId = playerId
        
        viewModelScope.launch {
            repository.listenToRoom(roomCode).collect { room ->
                if (room == null) return@collect

                val isMyTurn = room.activePlayerId == currentPlayerId
                val activeName = room.players[room.activePlayerId]?.name ?: "Someone"
                
                _uiState.update { it.copy(
                    isMyTurn = isMyTurn,
                    activePlayerName = if (isMyTurn) "It's your turn!" else "Waiting for $activeName..."
                ) }

                if (room.status == "finished" && _uiState.value.status == GameStatus.PLAYING) {
                    _uiState.update { it.copy(status = GameStatus.LOST, eventLog = "Game Over! Another player reached the goal.") }
                }
            }
        }
    }

    fun onSave() {
        if (!_uiState.value.isMyTurn) return
        executeTurn { currentBalance ->
            val interest = (currentBalance * 0.05).toInt()
            val newBalance = currentBalance + interest
            Pair(newBalance, "Action: Saved money (+$interest).")
        }
    }

    fun onInvest() {
        if (!_uiState.value.isMyTurn) return
        executeTurn { currentBalance ->
            val investmentCost = 200
            if (currentBalance < investmentCost) {
                Pair(currentBalance, "Action: Not enough balance to invest.")
            } else {
                val win = Random.nextBoolean()
                if (win) {
                    Pair(currentBalance + 200, "Action: Invested $200 and won! Net gain +$200.")
                } else {
                    Pair(currentBalance - 200, "Action: Invested $200 and lost. Net loss -$200.")
                }
            }
        }
    }

    fun onSpend() {
        if (!_uiState.value.isMyTurn) return
        executeTurn { currentBalance ->
            val spent = 150
            Pair(currentBalance - spent, "Action: Spent $150 on lifestyle.")
        }
    }

    private fun executeTurn(actionLogic: (Int) -> Pair<Int, String>) {
        if (_uiState.value.status != GameStatus.PLAYING || !_uiState.value.isMyTurn) return

        _uiState.update { currentState ->
            val (balanceAfterAction, actionMsg) = actionLogic(currentState.balance)
            
            var finalBalance = balanceAfterAction
            var eventMsg = ""
            if (Random.nextFloat() < 0.20f) {
                val randomEvent = getRandomEvents().random()
                finalBalance += randomEvent.amount
                eventMsg = " | Event: ${randomEvent.description} (${if(randomEvent.amount >= 0) "+" else ""}${randomEvent.amount})"
            }

            val newStatus = when {
                finalBalance >= currentState.target -> GameStatus.WON
                finalBalance <= 0 -> GameStatus.LOST
                else -> GameStatus.PLAYING
            }

            viewModelScope.launch {
                repository.updatePlayerMoney(currentRoomCode, currentPlayerId, finalBalance)
                
                if (newStatus == GameStatus.WON) {
                    repository.endGame(currentRoomCode, currentPlayerId)
                } else {
                    repository.passTurn(currentRoomCode, currentPlayerId)
                }
            }

            currentState.copy(
                balance = finalBalance,
                status = newStatus,
                turn = if (newStatus == GameStatus.PLAYING) currentState.turn + 1 else currentState.turn,
                eventLog = "Turn ${currentState.turn}: $actionMsg$eventMsg"
            )
        }
    }
}
