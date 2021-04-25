package no.ntnu.beardblaster.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*

private val log = logger<BeardScale>()

class BeardScale : Table(Scene2DSkin.defaultSkin) {
    private val scale = scene2d.image(skin[Image.BeardScale]) {

    }

    init {
        add(scene2d.label("0cm", LabelStyle.Body.name) {
            background = dimmedLabelBackground()
        }).pad(5f).left()
        add(scene2d.label("100+cm", LabelStyle.Body.name) {
            background = dimmedLabelBackground()
        }).pad(5f).right()
        row()
        add(scene2d.table {
            background = skin[Image.BarContainer]
            pad(25f)
            add(scale).growX()
        }).colspan(2).growX()
        }

    companion object {
        private val START_COLOR: Color = Color.CYAN
        private val MID_COLOR: Color = Color.YELLOW
        private val END_COLOR: Color = Color.MAGENTA

        fun getBeardColor(beardLength: Float): Color {
            val frac = beardLength / 100
            val color = when {
                frac < 0.5 -> interpolate(START_COLOR, MID_COLOR, frac * 2)
                else -> interpolate(MID_COLOR, END_COLOR, frac - (1 - frac))
            }
            log.debug { "Beard of length $beardLength ($frac) got assigned color $color." }
            return color
        }

        private fun interpolate(start: Color, end: Color, frac: Float): Color {
            return when {
                frac <= 0.0 -> start
                frac >= 1.0 -> end
                else -> {
                    Color(
                        start.r + (end.r - start.r) * frac,
                        start.g + (end.g - start.g) * frac,
                        start.b + (end.b - start.b) * frac,
                        start.a + (end.a - start.a) * frac,
                    )
                }
            }
        }
    }
}
