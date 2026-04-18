package com.ud.taller2.data.model

/**
 * Available actions a player can take during their turn
 */
enum class ActionType {
    SAVE,       // Safe money with fixed return
    INVEST,     // Risk money for chance of higher return
    SPEND       // Spend money (always reduces balance)
}