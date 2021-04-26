package no.ntnu.beardblaster.repositories

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
import no.ntnu.beardblaster.commons.user.AbstractRepository
import no.ntnu.beardblaster.commons.user.User

class UserRepository(private val db: FirebaseFirestore = Firebase.firestore) : AbstractRepository<User> {
    private val TAG = "UserRepository"

    override fun getDocument(id: String, collection: String): Flow<State<User>> = flow {
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
        if (snapshot.exists()) {
            val doc = snapshot.toObject<User>()
            if (doc != null) {
                emit(State.success(doc))
            }
        } else {
            emit(State.failed<User>("Document does not exist"))
        }
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    override fun create(doc: User, collection: String): Flow<State<User>> = flow {
        emit(State.loading<User>())

        if (collection.isNotEmpty()) {
            if (doc.hasId()) {
                Log.d(TAG, "Updating document with id ${doc.id}")

                db.collection(collection).document(doc.id).set(doc).await()
                emit(State.success(doc))
            } else {
                Log.d(TAG, "Adding document in $collection")
                val newDocRef = db.collection(collection).add(doc).await()
                doc.id = newDocRef.id
                emit(State.success(doc))
            }
        } else {
            val message = "No collection specified!"
            Log.w(TAG, message)
            emit(State.failed<User>(message))
        }
    }
}
