package no.ntnu.beardblaster.ui

import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.image
import ktx.scene2d.scene2d

class BeardScale : Table(Scene2DSkin.defaultSkin) {
    private val scale = scene2d.image(skin[Image.BeardScale])

    init {
        background = skin[Image.BarContainer]
        pad(25f)
        add(scale).growX()
    }
}
