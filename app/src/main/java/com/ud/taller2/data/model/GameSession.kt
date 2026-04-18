package com.ud.taller2.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GameSession(
    val gameId: String = "",
    val roomCode: String = "",
    val players: MutableMap<String, PlayerRoom> = mutableMapOf(),
    val currentTurn: Int = 1,
    val activePlayerId: String = "",
    val status: String = "active",
    val winnerId: String = "",
    val goalMoney: Int = 5000,
    val createdAt: Long = 0L
)