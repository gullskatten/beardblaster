package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.DocumentType

class SpellCast(
    val chosenSpell: Int,
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        0
    )
}

