package no.ntnu.beardblaster

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import no.ntnu.beardblaster.commons.game.AbstractLobbyRepository
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.GameOpponent
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

private val availableCodeCharacters: List<Char> = ('0'..'9').toList()

class LobbyRepository(private val db: FirebaseFirestore = Firebase.firestore) :
    AbstractLobbyRepository<Game> {
    private val TAG = "LobbyRepository"
    private val GAME_COLLECTION = "game"

    override fun joinLobbyWithCode(code: String, opponent: GameOpponent): Flow<State<Game>> = flow {
        emit(State.loading())

        if (code.isEmpty()) {
            val message = "Lobby code cannot be empty"
            Log.e(TAG, message)
            emit(State.failed(message))
            return@flow
        }
        val startOfToday: LocalDateTime = LocalDate.now(ZoneOffset.UTC).atStartOfDay()

        // Query for a game that has code XXX and was created today (to limit possibility for duplicate codes)
        val snapshot = db
            .collection(GAME_COLLECTION)
            .whereEqualTo("code", code)
            .whereGreaterThan("createdAt", startOfToday.toEpochSecond(ZoneOffset.UTC))
            .limit(1)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            // Find first element (limit: 1)
            val doc = snapshot.documents[0]
            // Update the document with a new opponent
            db.collection(GAME_COLLECTION).document(doc.id).update("opponent", opponent).await()

            // Parse the document found
            val game = doc.toObject<Game>()
            if (game != null) {
                // Id is set to document id
                game.id = doc.id
                // Emit the game
                emit(State.success(game))
            }
        } else {
            emit(State.failed<Game>("Lobby not found"))
        }
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToLobbyUpdates(id: String): Flow<State<Game>> = callbackFlow {
        if (id.isEmpty()) {
            val message = "Id of document cannot be empty"
            Log.e(TAG, message)
            offer(State.failed(message))
            awaitClose {  }
            return@callbackFlow
        }
        val subscription = db
            .collection(GAME_COLLECTION)
            .document(id)
            .addSnapshotListener { snapshot, _ ->
                Log.i(TAG, "Game was updated externally! Checking if snapshot exists")
                if (snapshot!!.exists()) {
                    Log.i(TAG, "Serializing Game object")
                    val updatedGameObject = snapshot.toObject<Game>()
                    if (updatedGameObject != null) {
                        Log.i(TAG, "Pushing offer of new game object.")
                        // Ensure id is set on game object
                        updatedGameObject.id = snapshot.id
                        offer(State.Success(updatedGameObject))
                    }
                } else {
                    offer(State.Failed<Game>("The lobby has been deleted."))
                }
            }

        // Required: Else stream closes immediately!
        //Finally if collect is not in use or collecting any data we cancel this channel to prevent any leak and remove the subscription listener to the database
        awaitClose { subscription.remove() }
    }

    override fun createLobby(): Flow<State<Game>> = flow {
        emit(State.loading<Game>())
        val code = (1..6)
            .map { i -> kotlin.random.Random.nextInt(0, availableCodeCharacters.size) }
            .map(availableCodeCharacters::get)
            .joinToString("");

        Log.d(TAG, "Creating a new game (lobby) with code $code")

        val doc = Game(
            code,
            createdAt = LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC),
            started = 0,
            ended = 0,
        )

        val newDocRef = db.collection(GAME_COLLECTION).add(doc).await()

        Log.d(TAG, "Lobby created successfully")

        doc.id = newDocRef.id
        emit(State.success(doc))
    }

    override fun cancelLobbyWithId(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading<Boolean>())
        Log.d(TAG, "Removing game with id $id")
        db.collection(GAME_COLLECTION).document(id).delete().await()
        emit(State.success(true))
    }

    override fun startGame(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading<Boolean>())
        try {
            db.collection(GAME_COLLECTION).document(id).update(
                "started", LocalDateTime.now(ZoneOffset.UTC)
                    .toEpochSecond(ZoneOffset.UTC)
            ).await()
            emit(State.success(true))
        } catch (e: Exception) {
            emit(State.failed<Boolean>("Failed to start game!"))
        }
    }

    override fun endGame(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading<Boolean>())
        try {
            db.collection(GAME_COLLECTION)
                .document(id)
                .update(
                    "ended", LocalDateTime.now(ZoneOffset.UTC)
                        .toEpochSecond(ZoneOffset.UTC)
                )
                .await()
            emit(State.success(true))
        } catch (e: Exception) {
            emit(State.failed<Boolean>("Failed to end game!"))
        }
    }

    override fun leaveLobbyWithId(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading<Boolean>())
        try {
            db.collection(GAME_COLLECTION)
                .document(id)
                .update(
                    "opponent", null
                )
                .await()
            emit(State.success(true))
        } catch (e: Exception) {
            emit(State.failed<Boolean>("Failed to leave lobby!"))
        }
    }
}
