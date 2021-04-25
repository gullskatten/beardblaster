package no.ntnu.beardblaster.leaderboard

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d
import no.ntnu.beardblaster.commons.leaderboard.BeardScore
import no.ntnu.beardblaster.ui.LabelStyle

class LeaderBoard : Table(Scene2DSkin.defaultSkin) {
    init {
        defaults().space(10f)
        columnDefaults(0).align(Align.left).expandX()
        columnDefaults(1).align(Align.right)
    }

    fun addScore(score: BeardScore) {
        addScore(score.displayName, score.beardLength)
    }

    private fun addScore(name: String, length: Float) {
        add(scoreLabel(name).apply { wrap = true })
        add(scoreLabel("$length cm").apply {
            color = BeardScale.getBeardColor(length)
        })
        row()
    }

    private fun scoreLabel(text: String): Label {
        return scene2d.label(text, LabelStyle.LightText.name) {
            setFontScale(1.5f)
        }
    }
}
