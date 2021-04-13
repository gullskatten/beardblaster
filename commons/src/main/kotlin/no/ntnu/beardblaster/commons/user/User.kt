package no.ntnu.beardblaster.commons.user

import no.ntnu.beardblaster.commons.user.DocumentType


data class User(val displayName: String = "Stranger", var beardLength: Float = 0f, override var id: String = "") : DocumentType {

}
