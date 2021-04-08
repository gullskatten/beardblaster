package no.ntnu.beardblaster.firestore

import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.AbstractFirestore
import no.ntnu.beardblaster.commons.DocumentType
import no.ntnu.beardblaster.commons.State
import pl.mk5.gdx.fireapp.PlatformDistributor

class Firestore<T : DocumentType> : PlatformDistributor<AbstractFirestore<T>>(), AbstractFirestore<T> {

    override fun getIOSClassName(): String {
        TODO("Not yet implemented")
    }

    override fun getAndroidClassName(): String {
        return "no.ntnu.beardblaster.Firestore"
    }

    override fun getWebGLClassName(): String {
        TODO("Not yet implemented")
    }

    override fun create(doc: T, collection: String): T {
        return platformObject.create(doc, collection)
    }

    override fun getDocument(id: String, collection: String): Flow<State<T>> {
        return platformObject.getDocument(id, collection)
    }



}