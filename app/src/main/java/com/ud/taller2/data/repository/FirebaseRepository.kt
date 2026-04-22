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

    private fun generateRoomCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    suspend fun createRoom(hostName: String, hostId: String): Result<String> {
        return try {
            val roomCode = generateRoomCode()
            val hostPlayer = PlayerRoom(
                name = hostName,
                money = 1000,
                ready = true,
                host = true
            )

            val room = Room(
                roomCode = roomCode,
                hostName = hostName,
                hostId = hostId,
                createdAt = System.currentTimeMillis(),
                status = "waiting",
                players = mapOf(hostId to hostPlayer),
                activePlayerId = hostId // Host starts the game
            )

            database.child("rooms").child(roomCode).setValue(room).await()
            Result.success(roomCode)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun joinRoom(roomCode: String, playerName: String): Result<String> {
        return try {
            val playerId = UUID.randomUUID().toString()
            val roomRef = database.child("rooms").child(roomCode)

            val snapshot = roomRef.get().await()
            if (!snapshot.exists()) return Result.failure(Exception("Room not found"))

            val room = snapshot.getValue(Room::class.java)
            if (room?.status != "waiting") return Result.failure(Exception("Game already started"))
            if ((room?.players?.size ?: 0) >= (room?.maxPlayers ?: 3)) return Result.failure(Exception("Room is full"))

            val player = PlayerRoom(
                name = playerName,
                money = 1000,
                ready = false,
                host = false
            )

            roomRef.child("players").child(playerId).setValue(player).await()
            Result.success(playerId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listenToRoom(roomCode: String): Flow<Room?> = callbackFlow {
        val roomRef = database.child("rooms").child(roomCode)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Room::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        roomRef.addValueEventListener(listener)
        awaitClose { roomRef.removeEventListener(listener) }
    }

    suspend fun setPlayerReady(roomCode: String, playerId: String, isReady: Boolean) {
        database.child("rooms").child(roomCode).child("players").child(playerId).child("ready").setValue(isReady).await()
    }

    suspend fun updatePlayerMoney(roomCode: String, playerId: String, newMoney: Int) {
        database.child("rooms").child(roomCode).child("players").child(playerId).child("money").setValue(newMoney).await()
    }

    suspend fun startGame(roomCode: String) {
        database.child("rooms").child(roomCode).child("status").setValue("playing").await()
    }

    suspend fun passTurn(roomCode: String, currentPlayerId: String) {
        try {
            val snapshot = database.child("rooms").child(roomCode).get().await()
            val room = snapshot.getValue(Room::class.java) ?: return
            
            val playerIds = room.players.keys.toList().sorted()
            val currentIndex = playerIds.indexOf(currentPlayerId)
            val nextIndex = (currentIndex + 1) % playerIds.size
            val nextPlayerId = playerIds[nextIndex]
            
            database.child("rooms").child(roomCode).child("activePlayerId").setValue(nextPlayerId).await()
        } catch (e: Exception) {
            // Log error
        }
    }

    suspend fun endGame(roomCode: String, winnerId: String) {
        database.child("rooms").child(roomCode).child("status").setValue("finished").await()
        database.child("rooms").child(roomCode).child("winnerId").setValue(winnerId).await()
    }

    suspend fun leaveRoom(roomCode: String, playerId: String) {
        database.child("rooms").child(roomCode).child("players").child(playerId).removeValue().await()
    }
}
