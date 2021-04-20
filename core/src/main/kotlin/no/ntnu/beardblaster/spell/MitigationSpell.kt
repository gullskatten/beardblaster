package no.ntnu.beardblaster.spell

abstract class MitigationSpell(
    val wizardId: String,
    val activatedAtTurn: Int,
    val duration: Int,
    val amount: Int
) {
    abstract fun isActive(turn: Int, currentWizardId: String?, opponentWizardId: String?): Boolean
    abstract fun applySpellMitigation(spellAction: SpellAction?)
}
