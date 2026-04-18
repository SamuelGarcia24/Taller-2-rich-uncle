package com.ud.taller2.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Room(
    val roomCode: String = "",
    val hostName: String = "",
    val hostId: String = "",
    val createdAt: Long = 0L,
    val status: String = "waiting", // waiting, playing, finished
    val players: MutableMap<String, PlayerRoom> = mutableMapOf(),
    val currentTurn: Int = 0,
    val maxPlayers: Int = 3
)

@IgnoreExtraProperties
data class PlayerRoom(
    val name: String = "",
    val money: Int = 1000,
    val isReady: Boolean = false,
    val isHost: Boolean = false
)