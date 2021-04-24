package no.ntnu.beardblaster.game

import no.ntnu.beardblaster.commons.game.Game

class GameData private constructor() {
    var game: Game? = null
    var isHost: Boolean = false
    var error: String? = null
        private set

    companion object {
        val instance = GameData()
    }
}
