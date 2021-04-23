package no.ntnu.beardblaster

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
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
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.AbstractGameRepository
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.Prize
import no.ntnu.beardblaster.commons.game.Turn
import no.ntnu.beardblaster.commons.spell.SpellAction
import no.ntnu.beardblaster.game.GameData
import no.ntnu.beardblaster.user.UserData
import java.time.LocalDateTime
import java.time.ZoneOffset


class GameRepository(private val db: FirebaseFirestore = Firebase.firestore) :
    AbstractGameRepository<Game> {

    private val TAG = "GameRepository"
    private val GAME_COLLECTION = "games"
    private val TURNS_COLLECTION = "turns"
    private val SPELLS_COLLECTION = "spells"

    override fun castSpell(currentTurn: Int, spell: SpellAction): Flow<State<SpellAction>> = flow {
        emit(State.loading<SpellAction>())
        Log.i(TAG, "Pushing spell to Firebase")

        val newSpellDocRef = db.collection(GAME_COLLECTION)
            .document(GameData.instance.game!!.id)
            .collection(TURNS_COLLECTION)
            .document(currentTurn.toString())
            .collection(SPELLS_COLLECTION)
            .document(UserData.instance.user!!.id)

        newSpellDocRef.set(spell).await()

        Log.d(TAG, "Spell created successfully")

        spell.docId = newSpellDocRef.id
        emit(State.success(spell))
    }

    override fun createTurn(currentTurn: Int): Flow<State<Turn>> = flow {

        emit(State.loading<Turn>())
        Log.i(TAG, "Creating turn $currentTurn")
        val doc = Turn()
        val documentReference = db.collection(GAME_COLLECTION)
            .document(GameData.instance.game!!.id)
            .collection(TURNS_COLLECTION)
            .document("$currentTurn")

        val turnDocument = documentReference.get().await()

        if (!turnDocument.exists()) {
            documentReference.set(doc).await()
        }
        Log.d(TAG, "Created turn $currentTurn successfully")

        doc.id = documentReference.id
        emit(State.success(doc))
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToGameUpdates(id: String): Flow<State<Game>> = callbackFlow {
        if (id.isEmpty()) {
            val message = "Id of document cannot be empty"
            Log.e(TAG, message)
            offer(State.failed(message))
            awaitClose { }
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
                    offer(State.Failed<Game>("The game has been deleted."))
                }
            }

        // Required: Else stream closes immediately!
        //Finally if collect is not in use or collecting any data we cancel this channel to prevent any leak and remove the subscription listener to the database
        awaitClose { subscription.remove() }
    }

    @ExperimentalCoroutinesApi
    override fun subscribeToSpellsOnTurn(collection: String): Flow<State<SpellAction>> =
        callbackFlow {
            if (collection.isEmpty()) {
                val message = "Id of document cannot be empty"
                Log.e(TAG, message)
                offer(State.failed(message))
                awaitClose { }
                return@callbackFlow
            }
            val subscription = db
                .collection(collection)
                .addSnapshotListener { snapshot, _ ->
                    Log.i(
                        TAG,
                        "Spell snapshot was updated externally! Documents: ${snapshot?.documents?.size} ,  DocumentChanges: ${snapshot?.documentChanges?.size}"
                    )
                    snapshot!!.documents.map { s: DocumentSnapshot -> s.toObject<SpellAction>() }
                        .forEach { spell -> offer(State.Success(spell!!)) }
                }
            // Required: Else stream closes immediately!
            //Finally if collect is not in use or collecting any data we cancel this channel to prevent any leak and remove the subscription listener to the database
            awaitClose { subscription.remove() }
        }

    override fun endGame(id: String): Flow<State<Boolean>> = flow {
        emit(State.loading<Boolean>())
        try {
            db.collection(GAME_COLLECTION)
                .document(id)
                .update(
                    "endedAt", LocalDateTime.now(ZoneOffset.UTC)
                        .toEpochSecond(ZoneOffset.UTC)
                )
                .await()
            emit(State.success(true))
        } catch (e: Exception) {
            emit(State.failed<Boolean>("Failed to end game!"))
        }
    }

    override fun distributePrizes(prizes: List<Prize>): Flow<State<Boolean>> = flow {
        emit(State.loading<Boolean>())
        try {
            db.collection(GAME_COLLECTION)
                .document(GameData.instance.game!!.id)
                .update(
                    "endedAt", LocalDateTime.now(ZoneOffset.UTC)
                        .toEpochSecond(ZoneOffset.UTC),
                    "prizes",
                    prizes
                )
                .await()
            emit(State.success(true))
        } catch (e: Exception) {
            emit(State.failed<Boolean>("Failed to end game and distribute prizes!"))
        }
    }
}
