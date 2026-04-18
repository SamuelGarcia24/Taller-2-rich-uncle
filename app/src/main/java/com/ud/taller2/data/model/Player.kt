package com.ud.taller2.data.model

/**
 * Represents a player in the game
 * @property name Player's display name
 * @property money Current money amount
 * @property isEliminated Whether the player has been eliminated (money <= 0)
 */
data class Player(
    val name: String = "Rich Uncle",
    var money: Int = 1000,
    val isEliminated: Boolean = false
)