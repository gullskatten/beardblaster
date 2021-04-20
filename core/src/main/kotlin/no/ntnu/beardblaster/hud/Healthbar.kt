package no.ntnu.beardblaster.ui

import ktx.scene2d.scene2d
import ktx.scene2d.table

class Healthbar {
    private val barMaxWidth = 280f

    val healthbar = scene2d.table {
        background = skin[Image.HPBar]
    }

    val healthbarContainer = scene2d.table {
        background = skin[Image.BarContainer]
        add(
            healthbar
        ).width(barMaxWidth).height(40f)
    }

    fun updateWidth(currentHp: Int, maxHp: Int) {
        healthbar.width = currentHp.toFloat() / maxHp.toFloat() * barMaxWidth
    }
}
