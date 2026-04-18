package com.ud.taller2.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ud.taller2.ui.createroom.CreateRoomScreen
import com.ud.taller2.ui.game.GameScreen
import com.ud.taller2.ui.gameover.GameOverScreen
import com.ud.taller2.ui.home.HomeScreen
import com.ud.taller2.ui.joinroom.JoinRoomScreen
import com.ud.taller2.ui.lobby.LobbyScreen
import com.ud.taller2.ui.victory.VictoryScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CreateRoom : Screen("create_room")
    object JoinRoom : Screen("join_room")
    object Lobby : Screen("lobby/{roomCode}/{playerId}") {
        fun createRoute(roomCode: String, playerId: String) = "lobby/$roomCode/$playerId"
    }
    object Game : Screen("game/{roomCode}/{playerId}") {
        fun createRoute(roomCode: String, playerId: String) = "game/$roomCode/$playerId"
    }
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

        // Create Room Screen
        composable(Screen.CreateRoom.route) {
            CreateRoomScreen(navController = navController)
        }

        // Join Room Screen
        composable(Screen.JoinRoom.route) {
            JoinRoomScreen(navController = navController)
        }

        // Lobby Screen
        composable(
            route = Screen.Lobby.route,
            arguments = listOf(
                navArgument("roomCode") { type = NavType.StringType },
                navArgument("playerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
            LobbyScreen(
                navController = navController,
                roomCode = roomCode,
                playerId = playerId
            )
        }

        // Game Screen
        composable(
            route = Screen.Game.route,
            arguments = listOf(
                navArgument("roomCode") { type = NavType.StringType },
                navArgument("playerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roomCode = backStackEntry.arguments?.getString("roomCode") ?: ""
            val playerId = backStackEntry.arguments?.getString("playerId") ?: ""
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