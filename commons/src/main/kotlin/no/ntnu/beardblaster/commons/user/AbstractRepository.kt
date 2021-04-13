package no.ntnu.beardblaster.commons.user

import kotlinx.coroutines.flow.Flow
import no.ntnu.beardblaster.commons.State

interface AbstractRepository<T> {
    fun getDocument(id: String, collection: String) : Flow<State<T>>
    fun create(doc: T, collection: String): Flow<State<T>>
}
