package no.ntnu.beardblaster.spell

import ktx.log.debug
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.spell.SpellAction
import no.ntnu.beardblaster.wizard.WizardState
import java.util.function.Consumer
import java.util.stream.Collectors

private val LOG = logger<SpellExecutor>()

class SpellExecutor {
    val spellHistory: MutableMap<Int, MutableList<SpellAction>> = HashMap()
    private val mitigationSpells: MutableList<MitigationSpell> = ArrayList()

    fun addSpell(spell: SpellAction, turn: Int) {
        if(isSpellValidForTurn(spell, turn)) {
            LOG.info { "Adding spell ${spell.spell.spellName} cast by ${spell.caster} - turn $turn" }
            if(spellHistory.containsKey(turn)) {
                spellHistory[turn]?.add(spell)
            }
            spellHistory.putIfAbsent(turn, mutableListOf(spell))
            if (spell.spell.spellHealing > 0) {
                mitigationSpells.add(HealingOverTime(spell.caster, turn, spell.spell.duration, spell.spell.spellHealing))
            }
            if (spell.spell.spellMitigation > 0) {
                mitigationSpells.add(
                    DamageReduction(
                        spell.caster,
                        turn + 1, // Damage reduction spells should be activated 1 turn after current
                        spell.spell.duration,
                        spell.spell.spellMitigation
                    )
                )
            }
            LOG.debug { "Current Spell History \n" }
            spellHistory.keys.forEach { i ->
                LOG.debug { "Turn $i" }
                spellHistory[i]!!.forEach {
                        item ->
                    LOG.debug { "Caster ${item.caster}: ${item.spell.spellName}" }
                }
            }
        }
    }

    private fun isSpellValidForTurn(spell: SpellAction, turn: Int): Boolean {
        if(!spellHistory.containsKey(turn)) {
            return true
        }
        return spellHistory[turn]!!.none { registeredSpell -> spell.caster == registeredSpell.caster }
    }

    // Actions are simply snapshots of spells that occurs (like a log of spells for a turn)
    fun getSpellResultForTurn(turn: Int, wizardState: WizardState): List<SpellActionWithAnimation> {
        // Since we need to handle mitigation spells that were included X turns ago,
        // we need to peek into our spells and update them with active mitigation spells.
        LOG.debug { "Getting Spell Result for turn - $turn" }

        if (!spellHistory.containsKey(turn)) {
            return listOf()
        }

        val actions = spellHistory[turn]!!.stream().peek { spellAction: SpellAction ->
            mitigationSpells
                .stream()
                .filter { mitigationSpell: MitigationSpell ->
                    mitigationSpell.isActive(
                        turn,
                        spellAction.caster,
                        spellAction.receiver
                    )
                }
                .forEach { activeMitigation: MitigationSpell ->
                    activeMitigation.applySpellMitigation(
                        spellAction
                    )
                }
        }.collect(Collectors.toList())

        actions.forEach(Consumer { spellAction: SpellAction ->
            wizardState.update(spellAction)
        })


            actions.forEach {
                    item ->
                LOG.debug { "Caster ${item.caster}" }
                LOG.debug { "Spell ${item.spell.spellName}" }
                LOG.debug { "CasterWizard: ${item.casterWizard?.displayName}" }
                LOG.debug { "CasterWizard HP: ${item.casterWizard?.getHealthPoints()}" }
                LOG.debug { "ReceiverWizard: ${item.receiverWizard?.displayName}" }
                LOG.debug { "Damage: ${item.damageDealt}" }
                LOG.debug { "Healing: ${item.healing}" }
                LOG.debug { "Absorbed: ${item.damageAbsorbed}" }
            }

        return actions.map { spellAction ->  SpellActionWithAnimation(spellAction)}
    }
}
