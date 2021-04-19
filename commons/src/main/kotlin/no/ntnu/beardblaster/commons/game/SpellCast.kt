package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.user.DocumentType

class SpellCast(
    val chosenSpell: Int,
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        0
    )
}

