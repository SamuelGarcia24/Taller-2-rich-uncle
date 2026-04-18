package com.ud.taller2.domain.model

/**
 * Represents the result of a player action
 */
sealed class ActionResult {
    /**
     * Successful action result
     * @property newMoney Updated money amount after action
     * @property message Description of what happened
     * @property isVictory Whether this action caused a victory
     * @property isGameOver Whether this action caused a game over
     */
    data class Success(
        val newMoney: Int,
        val message: String,
        val isVictory: Boolean = false,
        val isGameOver: Boolean = false
    ) : ActionResult()

    /**
     * Failed action result
     * @property message Error description
     */
    data class Error(
        val message: String
    ) : ActionResult()
}