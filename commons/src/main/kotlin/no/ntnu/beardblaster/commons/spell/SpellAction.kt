package no.ntnu.beardblaster.commons.spell

import no.ntnu.beardblaster.commons.wizard.Wizard

open class SpellAction(val spell: Spell, val caster: String, val receiver: String) {
    constructor() : this(
        Spell(0, "Forfeit", 0, 0, 0, "Opponent has left the game!", 1),
        "",
        ""
    )

    var docId = ""
    var damageDealt = spell.spellDamage
    var damageAbsorbed = 0
    var healing = spell.spellHealing
    var receiverWizard: Wizard? = null
    var casterWizard: Wizard? = null
    var isForfeit: Boolean = false
}
