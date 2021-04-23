package no.ntnu.beardblaster.commons.spell

import no.ntnu.beardblaster.commons.user.DocumentType

class Spell(
    val spellID: Int,
    val spellName: String,
    val spellHealing: Int,
    val spellDamage: Int,
    val spellMitigation: Int,
    val spellDescription: String,
    val duration: Int,
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        1,
        "",
        0,
        0, 0,
        "",
        0
    )
}

