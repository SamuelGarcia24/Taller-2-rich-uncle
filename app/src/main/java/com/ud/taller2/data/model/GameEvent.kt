package com.ud.taller2.data.model

data class GameEvent(
    val description: String,
    val amount: Int, // Positive for gain, negative for loss
    val isGoodNews: Boolean
)

fun getRandomEvents(): List<GameEvent> {
    return listOf(
        GameEvent("You won the lottery!", 1000, true),
        GameEvent("Stock market crash!", -500, false),
        GameEvent("Rental income received", 300, true),
        GameEvent("Property tax payment", -400, false),
        GameEvent("Inheritance from an uncle", 1500, true),
        GameEvent("Bank error in your favor", 200, true),
        GameEvent("Luxury dinner expense", -150, false),
        GameEvent("Traffic fine", -100, false)
    )
}