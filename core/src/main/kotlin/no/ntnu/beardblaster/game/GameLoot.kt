package no.ntnu.beardblaster.game

import no.ntnu.beardblaster.commons.game.Loot
import java.util.*

class GameLoot : Observable() {

    private var loot: List<Loot> = mutableListOf()

    fun setLoot(loot: List<Loot>) {
        this.loot = loot
        notifyObservers(loot)
        setChanged()
    }

    fun getLoot() : List<Loot> {
        return loot
    }
}
