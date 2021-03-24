package no.ntnu.beardblaster.commons

interface DocumentType {
    var id: String
    fun hasId(): Boolean {
        return id.isNotEmpty() && id.isNotBlank()
    }
    //abstract fun <DocumentType> fromHashMap(hashmap: HashMap<String, Any>);
}