package no.ntnu.beardblaster.commons

import kotlinx.coroutines.flow.Flow

interface AbstractRepository<T> {
    fun getDocument(id: String, collection: String) : Flow<State<T>>
    fun create(doc: T, collection: String): Flow<State<T>>
}