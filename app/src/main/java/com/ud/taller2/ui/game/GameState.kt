package com.ud.taller2.ui.game

data class GameState(
    val balance: Int = 1000,
    val target: Int = 5000,
    val turn: Int = 1,
    val status: GameStatus = GameStatus.PLAYING,
    val eventLog: String = "Welcome! Goal: Reach $5000.",
    val isMyTurn: Boolean = false,
    val activePlayerName: String = ""
)
