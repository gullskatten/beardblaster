package no.ntnu.beardblaster.models

class Wizard(private val beardLength : Int, private var hitPoints : Int = 30, private val wizType : MutableList<Int> = mutableListOf(1,2,3)){
    fun updateHP(hpChange : Int){
        hitPoints += hpChange
    }

    fun getElements(): MutableList<Int> {
        return wizType
    }

    fun getHP(): Int {
        return hitPoints
    }

    fun getBeardLength(): Int{
        return beardLength
    }
}
