package com.ud.taller2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ud.taller2.ui.game.GameScreen
import com.ud.taller2.ui.gameover.GameOverScreen
import com.ud.taller2.ui.home.HomeScreen
import com.ud.taller2.ui.victory.VictoryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Game : Screen("game")
    object Victory : Screen("victory/{finalMoney}/{turns}") {
        fun createRoute(finalMoney: Int, turns: Int) = "victory/$finalMoney/$turns"
    }
    object GameOver : Screen("gameover/{finalMoney}/{turns}") {
        fun createRoute(finalMoney: Int, turns: Int) = "gameover/$finalMoney/$turns"
    }
}

@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // Game Screen
        composable(Screen.Game.route) {
            GameScreen(navController = navController)
        }

        // Victory Screen
        composable(
            route = Screen.Victory.route,
            arguments = listOf(
                navArgument("finalMoney") { type = NavType.IntType },
                navArgument("turns") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val finalMoney = backStackEntry.arguments?.getInt("finalMoney") ?: 0
            val turns = backStackEntry.arguments?.getInt("turns") ?: 0
            VictoryScreen(
                navController = navController,
                finalMoney = finalMoney,
                turns = turns
            )
        }

        // Game Over Screen
        composable(
            route = Screen.GameOver.route,
            arguments = listOf(
                navArgument("finalMoney") { type = NavType.IntType },
                navArgument("turns") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val finalMoney = backStackEntry.arguments?.getInt("finalMoney") ?: 0
            val turns = backStackEntry.arguments?.getInt("turns") ?: 0
            GameOverScreen(
                navController = navController,
                finalMoney = finalMoney,
                turns = turns
            )
        }
    }
}