package no.ntnu.beardblaster.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d
import no.ntnu.beardblaster.commons.leaderboard.BeardScore

private val log = logger<LeaderBoard>()

class LeaderBoard : Table(Scene2DSkin.defaultSkin) {
    init {
        defaults().space(10f)
        columnDefaults(0).align(Align.left).expandX()
        columnDefaults(1).align(Align.right)
    }

    fun addScore(score: BeardScore) {
        addScore(score.displayName, score.beardLength)
    }

    fun addScore(name: String, length: Float) {
        add(scoreLabel(name).apply { wrap = true })
        add(scoreLabel("$length cm").apply { color = getBeardColor(length) })
        row()
    }

    private fun scoreLabel(text: String): Label {
        return scene2d.label(text, LabelStyle.LightText.name) {
            setFontScale(1.5f)
        }
    }

    private fun getBeardColor(beardLength: Float): Color {
        val frac = beardLength / 100
        val color = when {
            frac < 0.5 -> interpolate(Color.CYAN, Color.YELLOW, frac * 2)
            else -> interpolate(Color.YELLOW, Color.MAGENTA, frac - (1 - frac))
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
