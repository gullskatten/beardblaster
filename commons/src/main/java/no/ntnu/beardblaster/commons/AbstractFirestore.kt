package no.ntnu.beardblaster.commons

interface AbstractFirestore<T> {
    suspend fun getDocument(id: String, collection: String, fromHashMap: (data: HashMap<String, Any>) -> T) : T?
    fun create(doc: T, collection: String): T
}