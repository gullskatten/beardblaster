package no.ntnu.beardblaster

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import no.ntnu.beardblaster.commons.AbstractFirestore
import no.ntnu.beardblaster.commons.CreateDocumentFailureException
import no.ntnu.beardblaster.commons.DocumentType

class Firestore<T : DocumentType>(private val db: FirebaseFirestore = Firebase.firestore) : AbstractFirestore<T> {
    val TAG = "Firestore"

    override fun getDocument(ref: String, onSuccess: Function<T?>, onFail: Function<String>) {

        if (ref.isEmpty()) {
            onFail.run { "Document reference is empty." }
        }
        db
                .document(ref)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        onSuccess.run { toObjectRef(document) }
                    } else {
                        onFail.run { "Document not found!" }
                    }
                }
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
                            throw CreateDocumentFailureException(it)
                        }
            }
        } else {
            Log.w(TAG, "Cannot create document without specifying its collection!")
        }

        return doc
    }


    private inline fun <reified T : Any> toObjectRef(document: DocumentSnapshot): T? {
        return document.toObject<T>()
    }
}