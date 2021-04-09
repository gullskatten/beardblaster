package no.ntnu.beardblaster.models

class SpellCasting{
    private val elementIDs : MutableList<Int> = mutableListOf(0,0,0)

    fun getSpellID(): Int {
        //Unique spell IDs can be created by using the fundamental theorem of arithmetic, as long as all the distinct elementIDs are uniquely prime. The number of elements needed to be added for this to be a
        //terrible way of uniquely identifying spells is quite high.
        return elementIDs[0]* elementIDs[1]* elementIDs[2]
    }

    fun addElement(elementID : Int){
        //If there is an element with 0 in element_IDs, set the first 0 to the provided elementID
        if(0 in elementIDs){
            elementIDs[elementIDs.indexOfFirst{it == 0}] = elementID
        }
        //If not, shift the elements of the array to the left and replace the last index with the provided elementID
        else{
            for(i in 0..1) {
                elementIDs[i] = elementIDs[i + 1]
            }
            elementIDs[2] = elementID
        }
    }

    fun removeLast(){
        //Sets the value of the last non-zero element in the array to 0, basically removing the last user output
        elementIDs[elementIDs.indexOfLast{it != 0}] = 0
    }
}
