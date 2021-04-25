package no.ntnu.beardblaster.wizard

import no.ntnu.beardblaster.commons.spell.SpellAction
import no.ntnu.beardblaster.commons.wizard.Wizard
import no.ntnu.beardblaster.user.UserData

class WizardState(vararg opponents: Wizard) {
    val opponents: MutableMap<String, Wizard> = HashMap()

    fun update(spellAction: SpellAction) {
        if (spellAction.healing > 0) {
            opponents[spellAction.caster]!!.currentHealthPoints =
                (opponents[spellAction.caster]!!.currentHealthPoints + spellAction.healing).coerceAtMost(opponents[spellAction.caster]!!.maxHealthPoints)
        }
        if (spellAction.damageDealt > 0) {
            opponents[spellAction.receiver]!!
                .currentHealthPoints =
                (opponents[spellAction.receiver]!!.currentHealthPoints - spellAction.damageDealt).coerceAtLeast(
                    0
                )
        }

        spellAction.receiverWizard = opponents[spellAction.receiver]
        spellAction.casterWizard = opponents[spellAction.caster]
    }

    fun isAnyWizardDead(): Boolean {
        return opponents.values.any { w : Wizard -> w.currentHealthPoints <= 0 }
    }

    fun getCurrentUserAsWizard(): Wizard? {
        return opponents.values.find { wiz -> wiz.id == UserData.instance.user!!.id }
    }

    fun getEnemyAsWizard(): Wizard? {
        return opponents.values.find { wiz -> wiz.id != UserData.instance.user!!.id }
    }

    init {
        for (opponent in opponents) {
            this.opponents[opponent.id] = opponent
        }
    }
}
