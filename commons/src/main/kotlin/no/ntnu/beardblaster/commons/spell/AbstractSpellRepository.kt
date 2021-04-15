package no.ntnu.beardblaster.commons.spell

interface AbstractSpellRepository {
    fun getSpellById(id: Int): Spell?
    //fun readAllSpellData(): List<Spell?>
}
