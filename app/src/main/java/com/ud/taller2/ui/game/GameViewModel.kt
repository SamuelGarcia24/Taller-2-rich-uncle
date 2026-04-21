package com.ud.taller2.ui.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GameState())
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    fun onSave() {
        executeTurn { currentBalance ->
            val interest = (currentBalance * 0.05).toInt()
            val newBalance = currentBalance + interest
            Pair(newBalance, "Action: Saved money and earned 5% interest (+$interest).")
        }
    }

    fun onInvest() {
        executeTurn { currentBalance ->
            val investmentCost = 200
            if (currentBalance < investmentCost) {
                Pair(currentBalance, "Action: Not enough balance to invest $200.")
            } else {
                val win = Random.nextBoolean()
                if (win) {
                    val newBalance = currentBalance + 200 // -$200 cost + $400 prize = +$200 net
                    Pair(newBalance, "Action: Invested $200 and won! Net gain +$200.")
                } else {
                    val newBalance = currentBalance - 200 // -$200 cost = -$200 net
                    Pair(newBalance, "Action: Invested $200 and lost. Net loss -$200.")
                }
            }
        }
    }

    fun onSpend() {
        executeTurn { currentBalance ->
            val spent = 150
            val newBalance = currentBalance - spent
            Pair(newBalance, "Action: Spent $150 on lifestyle.")
        }
    }

    private fun executeTurn(actionLogic: (Int) -> Pair<Int, String>) {
        if (_uiState.value.status != GameStatus.PLAYING) return

        _uiState.update { currentState ->
            // 1. Apply Action Math
            val (balanceAfterAction, actionMsg) = actionLogic(currentState.balance)
            
            // 2. Roll for Random Event (20% probability)
            var finalBalance = balanceAfterAction
            var eventMsg = ""
            if (Random.nextFloat() < 0.20f) {
                val eventValue = Random.nextInt(-300, 301)
                finalBalance += eventValue
                eventMsg = if (eventValue >= 0) {
                    " | Random Event: Lucky day! +$$eventValue"
                } else {
                    " | Random Event: Unexpected bill! $$eventValue"
                }
            }

            // 3. Evaluate Win/Loss conditions immediately
            val newStatus = when {
                finalBalance >= currentState.target -> GameStatus.WON
                finalBalance <= 0 -> GameStatus.LOST
                else -> GameStatus.PLAYING
            }

            // 4. Update state
            currentState.copy(
                balance = finalBalance,
                status = newStatus,
                turn = if (newStatus == GameStatus.PLAYING) currentState.turn + 1 else currentState.turn,
                eventLog = "Turn ${currentState.turn}: $actionMsg$eventMsg"
            )
        }
    }
}
