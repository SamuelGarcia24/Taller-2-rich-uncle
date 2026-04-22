package com.ud.taller2.data.model

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName

@IgnoreExtraProperties
data class Room(
    val roomCode: String = "",
    val hostName: String = "",
    val hostId: String = "",
    val createdAt: Long = 0L,
    val status: String = "waiting", // waiting, playing, finished
    val players: Map<String, PlayerRoom> = emptyMap(),
    val currentTurn: Int = 0,
    val maxPlayers: Int = 3
)

@IgnoreExtraProperties
data class PlayerRoom(
    val name: String = "",
    val money: Int = 1000,
    @get:PropertyName("isReady") @set:PropertyName("isReady")
    var isReady: Boolean = false,
    @get:PropertyName("isHost") @set:PropertyName("isHost")
    var isHost: Boolean = false
)
