package no.ntnu.beardblaster.commons

interface AbstractFirestore<T> {
    fun getDocument(ref: String, onSuccess: Function<T?>, onFail: Function<String>)
    fun create(doc: T, collection: String): T
}