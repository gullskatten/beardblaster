package no.ntnu.beardblaster.spell

import no.ntnu.beardblaster.commons.spell.SpellAction
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class SpellExecutor {
    val spellHistory: MutableMap<Int, MutableList<SpellAction>> = HashMap()
    private val mitigationSpells: MutableList<MitigationSpell> = ArrayList()

    fun addSpell(spell: SpellAction, turn: Int) {
        spellHistory.computeIfPresent(turn) { _: Int?, spellsUsedInTurn: MutableList<SpellAction>? ->
            if (spellsUsedInTurn != null) {
                spellsUsedInTurn.add(spell)
                return@computeIfPresent spellsUsedInTurn
            } else {
                return@computeIfPresent mutableListOf(spell)
            }
        }
        spellHistory.putIfAbsent(turn, mutableListOf(spell))

        if (spell.spellData.spellHealing > 0) {
            mitigationSpells.add(HealingOverTime(spell.caster, turn, spell.spellData.duration, spell.spellData.spellHealing))
        }

        if (spell.spellData.spellMitigation > 0) {
            mitigationSpells.add(
                DamageReduction(
                    spell.caster,
                    turn + 1, // Damage reduction spells should be activated 1 turn after current
                    spell.spellData.duration,
                    spell.spellData.spellMitigation
                )
            )
        }
    }

    // Actions are simply snapshots of spells that occurs (like a log of spells for a turn)
    fun getSpellResultForTurn(turn: Int, wizardState: WizardState): List<SpellActionWithAnimation> {
        // Since we need to handle mitigation spells that were included X turns ago,
        // we need to peek into our spells and update them with active mitigation spells.
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

        return actions.map { spellAction ->  SpellActionWithAnimation(spellAction)}
    }


}
