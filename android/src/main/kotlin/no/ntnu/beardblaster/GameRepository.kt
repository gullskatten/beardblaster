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
import no.ntnu.beardblaster.commons.AbstractGameRepository
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.SpellCast
import no.ntnu.beardblaster.commons.game.Turn
import no.ntnu.beardblaster.game.GameData
import no.ntnu.beardblaster.user.UserData


class GameRepository(private val db: FirebaseFirestore = Firebase.firestore) :
    AbstractGameRepository<Game> {
    private val TAG = "GameRepository"
    private val GAME_COLLECTION = "games"
    private val TURNS_COLLECTION = "turns"
    private val SPELLS_COLLECTION = "spells"

    override fun createTurn(currentTurn: Int): Flow<State<Turn>> = flow {
        GameData.instance.game.let { }
        emit(State.loading<Turn>())
        val doc = Turn()
        val documentReference = db.collection(GAME_COLLECTION)
            .document(GameData.instance.game!!.id)
            .collection(TURNS_COLLECTION)
            .document("$currentTurn")

        val turnDocument = documentReference.get().await()

        if(!turnDocument.exists()) {
            documentReference.set(doc).await()
        }
        Log.d(TAG, "Created turn $currentTurn successfully")

        doc.id = documentReference.id
        emit(State.success(doc))
    }

    override fun endTurn(currentTurn: Int, chosenSpellId: Int): Flow<State<SpellCast>> = flow {
        GameData.instance.game.let { }
        emit(State.loading<SpellCast>())

        val turnDocRef = db.collection(GAME_COLLECTION)
            .document(GameData.instance.game!!.id)
            .collection(TURNS_COLLECTION)
            .document("$currentTurn")
        turnDocRef.update(
            if (GameData.instance.isHost) {
                "hostFinished"
            } else "opponentFinished", true
        ).await()

        val spellDoc = SpellCast(chosenSpell = chosenSpellId)
        val newSpellDocRef = db.collection(GAME_COLLECTION)
            .document(GameData.instance.game!!.id)
            .collection(TURNS_COLLECTION)
            .document(turnDocRef.id)
            .collection(SPELLS_COLLECTION)
            .document(UserData.instance.user!!.id)
        newSpellDocRef.set(spellDoc).await()

        Log.d(TAG, "Turn $currentTurn ended successfully")

        spellDoc.id = newSpellDocRef.id
        emit(State.success(spellDoc))
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
}
