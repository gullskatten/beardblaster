package no.ntnu.beardblaster.spell

import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.game.GamePlayer
import no.ntnu.beardblaster.commons.spell.Spell
import no.ntnu.beardblaster.commons.spell.SpellAction
import no.ntnu.beardblaster.commons.wizard.Wizard
import java.util.*
import java.util.function.Consumer

private val LOG = logger<FunctionalSpellTest>()

class FunctionalSpellTest {
    // generates a set of spells and wizards,
    // Wizard pick spells randomly
    // game is over when one wizards' health is reduced to 0.
    // log statements are used for verification.. :-)
    fun test() {
        val spellExecutor = SpellExecutor()

        val pureHealing = Spell(1, "healing", 5, 0, 0, "", 1)
        val healingAndMitigation = Spell(2, "healingM", 4, 0, 2, "", 1)
        val mitigation = Spell(3, "mitigation", 0, 0, 2, "", 3)
        val damage = Spell(4, "dmg", 0, 8, 0, "", 1)
        val mitigationAndDamage = Spell(5, "mitigationDmg", 0, 2, 4, "", 1)
        val healingAndDamage = Spell(5, "healingDmg", 2, 4, 0, "", 1)

        val spellList = listOf(
            pureHealing,
            healingAndDamage,
            healingAndMitigation,
            mitigation,
            damage,
            mitigationAndDamage
        )

        val me = Wizard(30, gamePlayer = GamePlayer("1", 15f, "Gandalf the White" ))
        val opponent = Wizard(30, GamePlayer("1", 15f, "Saruman the Wise" ))

        val wizardState = WizardState(me, opponent)

        val hasReachedZeroHp = booleanArrayOf(false)

        var turn = 0

        LOG.info { "BEGINNING! " }
        while (!hasReachedZeroHp[0]) {
            spellExecutor.addSpell(
                SpellAction(spellList[Random().nextInt(spellList.size - 1)], me.id,
                    opponent.id),
                turn,
            )
            spellExecutor.addSpell(
                SpellAction(spellList[Random().nextInt(spellList.size - 1)], opponent.id,
                    me.id),
                turn,
            )

            LOG.info { "<--- TURN $turn --->" }

            spellExecutor.getSpellResultForTurn(turn, wizardState)
                .forEach(Consumer { spellAction: SpellAction ->
                    Thread.sleep(500)

                    LOG.info {"\n" + spellAction.caster + " is casting " + spellAction.spell.spellName }
                    LOG.info {
                        "\n Caster: ${spellAction.caster} " +
                        "\n Receiver: ${spellAction.receiver} " +
                        "\n Absorbed: ${spellAction.damageAbsorbed} " +
                        "\n Dealt: ${spellAction.damageDealt}" +
                        "\n Healing: ${spellAction.healing}"
                    }
                    if (spellAction.damageAbsorbed > 0 && spellAction.damageAbsorbed >= spellAction.damageDealt) {
                        LOG.info{"All damage was absorbed by " + spellAction.receiver + "!" }
                    }
                    if (spellAction.healing > 0) {
                        LOG.info{spellAction.caster + " healed for " + spellAction.healing }
                    }
                    if (spellAction.damageDealt > 0) {
                        if (spellAction.damageAbsorbed > 0) {
                            LOG.info{spellAction.caster + " dealt " + spellAction.damageDealt + " (" + spellAction.damageAbsorbed + " absorbed)" }
                        } else {
                            LOG.info{spellAction.caster + " dealt " + spellAction.damageDealt }
                        }
                    }
                    LOG.info{"\n____________________________\n" }
                    LOG.info{"Remaining health: " }
                    wizardState.opponents.values.forEach(Consumer { w: Wizard -> LOG.info{w.id + ": " + w.currentHealthPoints } })
                    LOG.info{"\n____________________________\n" }
                    if (wizardState.opponents.values.any { w : Wizard -> w.currentHealthPoints <= 0 }) {
                        hasReachedZeroHp[0] = true
                        LOG.info{"\n____________________________\n" }
                        LOG.info{"      <--- Game ended --->    \n" }
                        LOG.info{spellAction.caster + " won!" }
                        LOG.info{"\n____________________________\n" }
                    }
                })
            turn++
        }
    }
}
