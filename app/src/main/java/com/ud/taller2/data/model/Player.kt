package com.ud.taller2.data.model

/**
 * Represents a player in the game
 */
data class Player(
    val name: String = "Rich Uncle",
    var money: Int = 1000,
    val isEliminated: Boolean = false
)