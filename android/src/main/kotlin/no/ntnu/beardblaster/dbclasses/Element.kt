package no.ntnu.beardblaster.dbclasses

import androidx.room.Entity
import androidx.room.PrimaryKey
import no.ntnu.beardblaster.commons.spell.Element
import no.ntnu.beardblaster.commons.spell.ElementList

@Entity(tableName = "element_table")
data class Element(
    @PrimaryKey(autoGenerate = false)
    val elementID : Int,
    val elementName : String
    )

{

   fun createElementList(input : List<no.ntnu.beardblaster.dbclasses.Element>): List<Element> {
        val output : MutableList<Element> = mutableListOf()
        var tempElement : Element
        for (i in input){
            tempElement = Element(elementID, elementName)
            output.add(tempElement)
        }
        return output
    }
}


