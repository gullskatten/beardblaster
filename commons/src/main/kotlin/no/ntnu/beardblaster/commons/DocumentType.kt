package no.ntnu.beardblaster.commons

interface DocumentType {
    var id: String
    fun hasId(): Boolean {
        return id.isNotEmpty() && id.isNotBlank()
    }
}