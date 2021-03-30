package no.ntnu.beardblaster

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import ktx.log.debug
import no.ntnu.beardblaster.commons.AbstractFirestore
import no.ntnu.beardblaster.commons.DocumentType
import no.ntnu.beardblaster.commons.FirestoreDocumentFailureException
import no.ntnu.beardblaster.commons.User


class Firestore<T : DocumentType>(private val db: FirebaseFirestore = Firebase.firestore) : AbstractFirestore<T> {
    val TAG = "Firestore"

    override suspend fun getDocument(id: String, collection: String, fromHashMap: (data: HashMap<String, Any>) -> T): T? {
        if (id.isEmpty() || collection.isEmpty()) {
            Log.e(TAG, "Document/collection cannot be empty. Was collection: $collection , document: $id")
        }

        val res = Tasks.await(
                db.collection(collection).document(id).get()
        )
        val userDocument = res.toObject<Any>() as HashMap<String, Any>
        return fromHashMap(userDocument)
    }

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