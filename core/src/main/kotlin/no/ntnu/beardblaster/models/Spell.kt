package no.ntnu.beardblaster.models

class Spell(val id: Int, val name: String, val damage: Int, description: String) {

    override fun toString(): String {
        return name
    }

}
