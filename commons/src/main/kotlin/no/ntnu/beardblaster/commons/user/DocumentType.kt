package no.ntnu.beardblaster.commons.user

interface DocumentType {
    var id: String
    fun hasId(): Boolean {
        return id.isNotEmpty() && id.isNotBlank()
    }
}
