package no.ntnu.beardblaster.spell

import no.ntnu.beardblaster.commons.spell.SpellAction

class HealingOverTime(wizardId: String, activatedAtTurn: Int, duration: Int, amount: Int) :
    MitigationSpell(wizardId, activatedAtTurn, duration, amount) {

    override fun isActive(turn: Int, currentWizardId: String?, opponentWizardId: String?): Boolean {
        return turn < activatedAtTurn + duration && currentWizardId != null && currentWizardId == wizardId
    }

    override fun applySpellMitigation(spellAction: SpellAction?) {
        spellAction?.healing = amount
    }
}
