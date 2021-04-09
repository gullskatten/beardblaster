package no.ntnu.beardblaster.commons


data class User(val displayName: String = "Stranger", var beardLength: Float = 0f, override var id: String = "") : DocumentType {

}
