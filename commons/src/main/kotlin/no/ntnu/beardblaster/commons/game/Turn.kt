package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.user.DocumentType

class Turn(
    var hostFinished: Boolean = false,
    var opponentFinished: Boolean = false,
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        false,
        false
    )
}

