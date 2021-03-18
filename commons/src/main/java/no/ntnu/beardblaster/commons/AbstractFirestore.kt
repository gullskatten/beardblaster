package no.ntnu.beardblaster.commons

interface AbstractFirestore<T> {
    fun getDocument(id: String, collection: String) : T?
    fun create(doc: T, collection: String): T
}