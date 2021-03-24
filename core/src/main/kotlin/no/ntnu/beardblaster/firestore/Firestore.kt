package no.ntnu.beardblaster.firestore

import no.ntnu.beardblaster.commons.AbstractFirestore
import no.ntnu.beardblaster.commons.DocumentType
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

    override fun getDocument(id: String, collection: String, fromHashMap: (data: HashMap<String, Any>) -> DocumentType): T? {
        return platformObject.getDocument(id, collection, fromHashMap)
    }



}