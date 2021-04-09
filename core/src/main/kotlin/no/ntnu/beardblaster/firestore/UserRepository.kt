package no.ntnu.beardblaster.firestore

import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.AbstractRepository
import no.ntnu.beardblaster.commons.DocumentType
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.User
import pl.mk5.gdx.fireapp.PlatformDistributor

class UserRepository : PlatformDistributor<AbstractRepository<User>>(), AbstractRepository<User> {

    override fun getIOSClassName(): String {
        TODO("Not yet implemented")
    }

    override fun getAndroidClassName(): String {
        return "no.ntnu.beardblaster.UserRepository"
    }

    override fun getWebGLClassName(): String {
        TODO("Not yet implemented")
    }

    override fun create(doc: User, collection: String): Flow<State<User>> {
        return platformObject.create(doc, collection)
    }

    override fun getDocument(id: String, collection: String): Flow<State<User>> {
        return platformObject.getDocument(id, collection)
    }



}