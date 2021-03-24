package no.ntnu.beardblaster

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import no.ntnu.beardblaster.commons.AbstractFirestore
import no.ntnu.beardblaster.commons.DocumentType
import no.ntnu.beardblaster.commons.FirestoreDocumentFailureException
import no.ntnu.beardblaster.commons.User


class Firestore<T : DocumentType>(private val db: FirebaseFirestore = Firebase.firestore) : AbstractFirestore<T> {
    val TAG = "Firestore"

    override fun getDocument(id: String, collection: String, fromHashMap: (data: HashMap<String, Any>) -> DocumentType): T? {
        println("hei")
        var c: T? = null
        if (id.isEmpty() || collection.isEmpty()) {
            Log.e(TAG, "Document/collection cannot be empty. Was collection: $collection , document: $id")
        }
        print("kommer vi oss hit?")
        db
                .collection(collection)
                .document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // TODO: 3/18/2021 - Throws exception
                        //  java.util.HashMap cannot be cast to no.ntnu.beardblaster.commons.DocumentType

                        val data = document.toObject<Any>() as HashMap<String, Any>
                        c = fromHashMap(data) as T
                        print(c)
                    } else {
                        c = null
                    }
                }
                .addOnCanceledListener { print("canceled") }
                .addOnFailureListener { print("failed")  }

        return c
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