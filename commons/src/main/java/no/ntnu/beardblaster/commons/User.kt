package no.ntnu.beardblaster.commons



data class User(val displayName: String = "Stranger", var beardLength: Number = 0, override var id: String = "") : DocumentType {
}