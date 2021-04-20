package no.ntnu.beardblaster.spell

import no.ntnu.beardblaster.commons.spell.Spell

class SpellAction(val spell: Spell, val caster: String, val receiver: String) {
    var damageDealt = spell.spellDamage
    var damageAbsorbed = 0
    var healing = spell.spellHealing
    var receiverWizard: WizardTemp? = null
    var casterWizard: WizardTemp? = null

}
