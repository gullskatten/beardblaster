package no.ntnu.beardblaster

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import no.ntnu.beardblaster.commons.*


class Firestore<T : DocumentType>(private val db: FirebaseFirestore = Firebase.firestore) : AbstractFirestore<T> {
    val TAG = "Firestore"

    override fun getDocument(id: String, collection: String): Flow<State<T>> = flow {
        emit(State.loading())

        if (id.isEmpty() || collection.isEmpty()) {
            Log.e(TAG, "Document/collection cannot be empty. Was collection: $collection , document: $id")
        }
        val snapshot = db
                .collection(collection)
                .document(id)
                .get()
                .await()

        emit(State.success(doc))
    }.catch {
        // If exception is thrown, emit failed state along with message.
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    override fun create(doc: T, collection: String): T {
        if (collection.isNotEmpty()) {
            if (doc.hasId()) {
                Log.d(TAG, "Updating document with id ${doc.id}")
                db.collection(collection).document(doc.id).set(doc)
            } else {
                Log.d(TAG, "Adding document in $collection")
                db.collection(collection).add(doc)
                        .addOnSuccessListener { documentRef ->
                            doc.id = documentRef.id
                        }
                        .addOnFailureListener {
                            Log.e(TAG, "Failed to create document at $collection : ${it.message}")
                            throw FirestoreDocumentFailureException(it)
                        }
            }
        } else {
            Log.w(TAG, "No collection specified!")
        }

        return doc
    }
}