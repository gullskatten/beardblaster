package no.ntnu.beardblaster.spell

import no.ntnu.beardblaster.commons.spell.SpellAction

class DamageReduction(wizardId: String, activatedAtTurn: Int, duration: Int, amount: Int) :
    MitigationSpell(wizardId, activatedAtTurn, duration, amount) {
    override fun isActive(
        turn: Int,
        currentWizardId: String?,
        opponentWizardId: String?
    ): Boolean {
        return turn < activatedAtTurn + duration && opponentWizardId != null && opponentWizardId == wizardId
    }

    override fun applySpellMitigation(spellAction: SpellAction?) {
        if(spellAction?.damageDealt != null) {
            spellAction.damageAbsorbed = amount
            spellAction.damageDealt = (spellAction.damageDealt - amount).coerceAtLeast(0)
        }
    }
}
