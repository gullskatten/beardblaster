package no.ntnu.beardblaster

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import no.ntnu.beardblaster.commons.*
import no.ntnu.beardblaster.commons.game.Game

class GameRepository(private val db: FirebaseFirestore = Firebase.firestore) : AbstractGameRepository<Game> {
    private val TAG = "UserRepository"

     fun getDocument(id: String, collection: String): Flow<State<Game>> = flow {
        emit(State.loading())

        if (id.isEmpty() || collection.isEmpty()) {
            val message = "Document/collection cannot be empty. Was collection: $collection , document: $id"
            Log.e(TAG, message)
            emit(State.failed(message))
        }
        val snapshot = db
                .collection(collection)
                .document(id)
                .get()
                .await()
        if(snapshot.exists()) {
            val doc = snapshot.toObject<Game>()
            if(doc != null) {
                emit(State.success(doc))
            }
        } else {
            emit(State.failed<Game>("Document does not exist"))
        }
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

     fun create(doc: Game, collection: String): Flow<State<Game>> = flow {
        emit(State.loading<Game>())

        if (collection.isNotEmpty()) {
            if (doc.hasId()) {
                Log.d(TAG, "Updating document with id ${doc.id}")

                val task = db.collection(collection).document(doc.id).set(doc)
                if(task.isSuccessful) {
                    emit(State.success(doc))
                } else if (task.isComplete && task.exception != null) {
                    emit(State.failed(task.exception?.message!!))
                }
            } else {
                Log.d(TAG, "Adding document in $collection")
                val newDocRef = db.collection(collection).add(doc).await()
                doc.id = newDocRef.id
                emit(State.success(doc))
            }
        } else {
            val message = "No collection specified!"
            Log.w(TAG, message)
            emit(State.failed<Game>(message))
        }
    }

    override fun validateLobbyCode(id: String, collection: String): Flow<State<Game>> {
        TODO("Not yet implemented")
    }

    override fun createLobby(): Flow<State<Game>> {
        TODO("Not yet implemented")
    }

    override fun cancelLobbyWithId(id: String) {
        TODO("Not yet implemented")
    }

    override fun startGame(): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun endGame(): Flow<State<Boolean>> {
        TODO("Not yet implemented")
    }
}