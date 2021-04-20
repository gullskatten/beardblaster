package no.ntnu.beardblaster.spell

import java.util.*

class WizardState(vararg opponents: WizardTemp) {
    val opponents: MutableMap<String, WizardTemp> = HashMap()
    fun update(spellAction: SpellAction) {
        if (spellAction.healing > 0) {
            opponents[spellAction.caster]!!.remainingHp =
                (opponents[spellAction.caster]!!.remainingHp + spellAction.healing).coerceAtMost(30)
        }
        if (spellAction.damageDealt > 0) {
            opponents[spellAction.receiver]!!
                .remainingHp =
                (opponents[spellAction.receiver]!!.remainingHp - spellAction.damageDealt).coerceAtLeast(
                    0
                )
        }
        spellAction.receiverWizard = opponents[spellAction.receiver]
        spellAction.casterWizard = opponents[spellAction.caster]
    }

    fun getOpponents(): List<WizardTemp> {
        return opponents.values as List<WizardTemp>
    }

    init {
        for (opponent in opponents) {
            this.opponents[opponent.wizardId] = opponent
        }
    }
}
