package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.user.DocumentType

class Turn(
    var initiator: String = "",
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        "",
        ""
    )
}

