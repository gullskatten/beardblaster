package no.ntnu.beardblaster.spell

import no.ntnu.beardblaster.commons.spell.Spell
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class SpellExecutor {
    private val spellHistory: MutableMap<Int, MutableList<SpellAction>> = HashMap()
    private val mitigationSpells: MutableList<MitigationSpell> = ArrayList()

    fun addSpell(spell: Spell, turn: Int, caster: String, receiver: String) {
        spellHistory.computeIfPresent(turn) { _: Int?, spellsUsedInTurn: MutableList<SpellAction>? ->
            if (spellsUsedInTurn != null) {
                spellsUsedInTurn.add(SpellAction(spell, caster, receiver))
                return@computeIfPresent spellsUsedInTurn
            } else {
                return@computeIfPresent mutableListOf(SpellAction(spell, caster, receiver))
            }
        }
        spellHistory.putIfAbsent(turn, mutableListOf(SpellAction(spell, caster, receiver)))

        if (spell.spellHealing > 0) {
            mitigationSpells.add(HealingOverTime(caster, turn, spell.duration, spell.spellHealing))
        }

        if (spell.spellMitigation > 0) {
            mitigationSpells.add(
                DamageReduction(
                    caster,
                    turn + 1, // Damage reduction spells should be activated 1 turn after current
                    spell.duration,
                    spell.spellMitigation
                )
            )
        }
    }

    fun getSpellResultForTurn(turn: Int, wizardState: WizardState): List<SpellAction> {
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
        actions.forEach(Consumer { spellAction: SpellAction -> wizardState.update(spellAction) })
        return actions
    }
}
