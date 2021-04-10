package no.ntnu.beardblaster

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ktx.async.KtxAsync
import no.ntnu.beardblaster.commons.AbstractLobbyRepository
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

private val availableCodeCharacters : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

class LobbyRepository(private val db: FirebaseFirestore = Firebase.firestore) : AbstractLobbyRepository<Game> {
    private val TAG = "LobbyRepository"

    override fun joinLobbyWithId(id: String): Flow<State<Game>> = flow {
        emit(State.loading())

        if (id.isEmpty()) {
            val message = "Lobby code cannot be empty"
            Log.e(TAG, message)
            emit(State.failed(message))
        }
        val startOfToday: LocalDateTime = LocalDate.now(ZoneOffset.UTC).atStartOfDay()

        // Query for a game that has code XXX and was created today (to limit possibility for duplicate codes)
        val snapshot = db
            .collection("game")
            .whereEqualTo(id, "lobbyCode")
            .whereGreaterThan("createdAt", startOfToday)
            .limit(1)
            .get()
            .await()

        if (!snapshot.isEmpty) {
            val doc = snapshot.documents[0].toObject<Game>()
            if (doc != null) {
                emit(State.success(doc))
            }
        } else {
            emit(State.failed<Game>("Lobby not found"))
        }
    }

    fun subscribe(id: String, obs: Observer): Flow<State<Game>> = flow {
        if (id.isEmpty()) {
            val message = "Lobby code cannot be empty"
            Log.e(TAG, message)
            emit(State.failed(message))
        }
        val snapshot = db
            .collection("game")
            .document(id)
            .addSnapshotListener(EventListener { value, error ->
                // TODO: Check out Kotlin ReceiveChannels : https://elizarov.medium.com/cold-flows-hot-channels-d74769805f9
            })
    }

    override fun createLobby(): Flow<State<Game>> = flow {
        emit(State.loading<Game>())
        val lobbyCode = (1..6)
            .map { i -> kotlin.random.Random.nextInt(0, availableCodeCharacters.size) }
            .map(availableCodeCharacters::get)
            .joinToString("");

        Log.d(TAG, "Creating a new game (lobby) with code $lobbyCode")

        val doc = Game(lobbyCode, createdAt = LocalDateTime.now(ZoneOffset.UTC))

        val newDocRef = db.collection("game").add(doc).await()

        Log.d(TAG, "Lobby created successfully")

        doc.id = newDocRef.id
        emit(State.success(doc))
    }

    override fun cancelLobbyWithId(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading<Boolean>())
        Log.d(TAG, "Removing lobby with id $id")
        db.collection("lobby").document(id).delete().await()
        emit(State.success(true))
    }

    override fun startGame(): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun endGame(): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }
}

object Listener : EventListener<DocumentSnapshot> {
    override fun onEvent(value: DocumentSnapshot?, error: FirebaseFirestoreException?) {
        value?.get("opponent")
    }
}
