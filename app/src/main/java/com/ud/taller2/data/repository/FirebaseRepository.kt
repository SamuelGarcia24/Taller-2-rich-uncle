package com.ud.taller2.data.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ud.taller2.data.model.PlayerRoom
import com.ud.taller2.data.model.Room
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRepository {

    private val database = FirebaseDatabase.getInstance().reference

    /**
     * Generate a random 6-character uppercase code
     * Excludes confusing characters: I, O, 0, 1
     */
    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }

    /**
     * Create a new room with a unique 6-character code
     */
    suspend fun createRoom(hostName: String): Result<String> {
        return try {
            val roomCode = generateRoomCode()
            val hostId = UUID.randomUUID().toString()

            val hostPlayer = PlayerRoom(
                name = hostName,
                money = 1000,
                isReady = true,
                isHost = true
            )

            val room = Room(
                roomCode = roomCode,
                hostName = hostName,
                hostId = hostId,
                createdAt = System.currentTimeMillis(),
                status = "waiting",
                players = mutableMapOf(hostId to hostPlayer)
            )

            database.child("rooms").child(roomCode).setValue(room).await()
            Result.success(roomCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Join an existing room
     */
    suspend fun joinRoom(roomCode: String, playerName: String): Result<String> {
        return try {
            val playerId = UUID.randomUUID().toString()
            val roomRef = database.child("rooms").child(roomCode)

            // Check if room exists
            val snapshot = roomRef.get().await()
            if (!snapshot.exists()) {
                return Result.failure(Exception("Room not found"))
            }

            val room = snapshot.getValue(Room::class.java)

            // Validate room state
            if (room?.status != "waiting") {
                return Result.failure(Exception("Game already started"))
            }

            if ((room?.players?.size ?: 0) >= (room?.maxPlayers ?: 3)) {
                return Result.failure(Exception("Room is full"))
            }

            // Check if player name already exists in room
            val nameExists = room?.players?.values?.any { it.name == playerName } == true
            if (nameExists) {
                return Result.failure(Exception("Player name already taken in this room"))
            }

            val player = PlayerRoom(
                name = playerName,
                money = 1000,
                isReady = false,
                isHost = false
            )

            roomRef.child("players").child(playerId).setValue(player).await()
            Result.success(playerId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Listen to room updates in real-time
     */
    fun listenToRoom(roomCode: String): Flow<Room?> = callbackFlow {
        val roomRef = database.child("rooms").child(roomCode)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val room = snapshot.getValue(Room::class.java)
                trySend(room)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        roomRef.addValueEventListener(listener)

        awaitClose {
            roomRef.removeEventListener(listener)
        }
    }

    /**
     * Update player ready status
     */
    suspend fun setPlayerReady(roomCode: String, playerId: String, isReady: Boolean) {
        database.child("rooms")
            .child(roomCode)
            .child("players")
            .child(playerId)
            .child("isReady")
            .setValue(isReady)
            .await()
    }

    /**
     * Check if all players are ready and start the game
     */
    suspend fun checkAllPlayersReady(roomCode: String): Boolean {
        return try {
            val snapshot = database.child("rooms")
                .child(roomCode)
                .child("players")
                .get()
                .await()

            var allReady = true
            snapshot.children.forEach { playerSnapshot ->
                val isReady = playerSnapshot.child("isReady").value as? Boolean ?: false
                if (!isReady) {
                    allReady = false
                }
            }

            if (allReady) {
                // Update room status to playing
                database.child("rooms")
                    .child(roomCode)
                    .child("status")
                    .setValue("playing")
                    .await()
            }

            allReady
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get current room data once
     */
    suspend fun getRoom(roomCode: String): Room? {
        return try {
            val snapshot = database.child("rooms")
                .child(roomCode)
                .get()
                .await()
            snapshot.getValue(Room::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Remove player from room
     */
    suspend fun leaveRoom(roomCode: String, playerId: String) {
        try {
            val roomRef = database.child("rooms").child(roomCode)
            val snapshot = roomRef.get().await()
            val room = snapshot.getValue(Room::class.java)

            // Remove player
            roomRef.child("players").child(playerId).removeValue().await()

            // Get updated player count
            val updatedSnapshot = roomRef.child("players").get().await()
            val playerCount = updatedSnapshot.childrenCount.toInt()

            // If room is empty, delete it
            if (playerCount == 0) {
                roomRef.removeValue().await()
                return
            }

            // If host left, assign new host
            if (room?.hostId == playerId) {
                updatedSnapshot.children.firstOrNull()?.let { newHostSnapshot ->
                    val newHostId = newHostSnapshot.key ?: ""
                    if (newHostId.isNotBlank()) {
                        roomRef.child("hostId").setValue(newHostId).await()
                        roomRef.child("hostName")
                            .setValue(newHostSnapshot.child("name").value as? String ?: "")
                            .await()
                        roomRef.child("players")
                            .child(newHostId)
                            .child("isHost")
                            .setValue(true)
                            .await()
                    }
                }
            }
        } catch (e: Exception) {
            // Log error if needed
        }
    }

    /**
     * Delete room when game ends or is abandoned
     */
    suspend fun deleteRoom(roomCode: String) {
        try {
            database.child("rooms").child(roomCode).removeValue().await()
        } catch (e: Exception) {
            // Log error if needed
        }
    }

    /**
     * Update player's money in the room
     */
    suspend fun updatePlayerMoney(roomCode: String, playerId: String, newMoney: Int) {
        try {
            database.child("rooms")
                .child(roomCode)
                .child("players")
                .child(playerId)
                .child("money")
                .setValue(newMoney)
                .await()
        } catch (e: Exception) {
            // Log error if needed
        }
    }

    /**
     * Check if room exists
     */
    suspend fun roomExists(roomCode: String): Boolean {
        return try {
            val snapshot = database.child("rooms")
                .child(roomCode)
                .get()
                .await()
            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }
    /**
     * Start the game (only host can call this)
     */
    suspend fun startGame(roomCode: String) {
        try {
            database.child("rooms")
                .child(roomCode)
                .child("status")
                .setValue("playing")
                .await()
        } catch (e: Exception) {
            // Log error if needed
        }
    }
}