package com.ud.taller2.data.model

/**
 * Represents the current state of the game
 * @property player The player data
 * @property currentTurn Current turn number
 * @property goalMoney Target money amount to win
 * @property lastActionResult Message describing the last action result
 * @property isGameActive Whether the game is still active
 * @property isVictory Whether the player has won
 * @property isGameOver Whether the player has lost
 */
data class GameState(
    val player: Player = Player(),
    val currentTurn: Int = 1,
    val goalMoney: Int = 5000,
    val lastActionResult: String = "Game started!",
    val isGameActive: Boolean = true,
    val isVictory: Boolean = false,
    val isGameOver: Boolean = false
)