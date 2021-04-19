package no.ntnu.beardblaster.commons.wizard

class Wizard(
    var beardLength: Float,
    var maxHP: Int = 30,
    private val wizType: MutableList<Int> = mutableListOf(1, 2, 3)
) {
    var currentHP = maxHP
    fun updateHP(hpChange: Int) {
        if (currentHP + hpChange >= maxHP) {
            currentHP = maxHP
        } else if (currentHP + hpChange <= 0) {
            currentHP = 0
        } else {
            currentHP += hpChange
        }
    }

    fun getElements(): MutableList<Int> {
        return wizType
    }

    fun wizardIsDead(): Boolean {
        return currentHP == 0
    }

    fun getCurrentHPString(): String {
        return "${currentHP}/${maxHP}"
    }
}
