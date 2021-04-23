package no.ntnu.beardblaster.commons.wizard

import no.ntnu.beardblaster.commons.game.GamePlayer

class Wizard(
    var maxHealthPoints: Int,
    id: String,
    beardLength: Float = 0f,
    displayName: String,
) : GamePlayer(id, beardLength, displayName) {
    constructor(maxHealthPoints: Int, gamePlayer: GamePlayer) :
        this(maxHealthPoints, gamePlayer.id, gamePlayer.beardLength, gamePlayer.displayName)
    var currentHealthPoints = maxHealthPoints

    fun isWizardDefeated(): Boolean {
        return currentHealthPoints <= 0
    }

    fun getHealthPoints(): String {
        return "${currentHealthPoints}/${maxHealthPoints}"
    }
}
