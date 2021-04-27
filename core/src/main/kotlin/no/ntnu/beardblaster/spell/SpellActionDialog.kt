package no.ntnu.beardblaster.spell

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.scene2d.*
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.leaderboard.BeardScale
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.LabelStyle
import no.ntnu.beardblaster.ui.get

@Scene2dDsl
class SpellActionDialog : Table(Scene2DSkin.defaultSkin), KTable {

    private val nameLabel: Label =
        scene2d.label(Nls.calculatingSpells(), LabelStyle.LightText.name) {
            setAlignment(Align.left)
            setFontScale(1.8f)
        }
    private val beardLength: Label =
        scene2d.label("", LabelStyle.LightText.name) {
            setAlignment(Align.right)
            setFontScale(1.8f)
        }
    private val descLabel: Label = scene2d.label(Nls.holdOn(), LabelStyle.LightText.name) {
        setAlignment(Align.center)
        setFontScale(1.5f)
        wrap = true
    }

    companion object {
        const val PADDING = 20f
        const val LABEL_WIDTH = 860f
    }

    fun updateNameLabelText(name: String) {
        nameLabel.setText(name)
    }

    fun updateBeardLengthLabelText(length: Float) {
        beardLength.setText("${length}cm")
        beardLength.color = BeardScale.getBeardColor(length)
    }

    fun updateDescLabelText(description: String) {
        descLabel.setText(description)
    }

    init {
        val wizNameTable = scene2d.table {
            defaults().space(30f)
        }
        wizNameTable.add(nameLabel)
        wizNameTable.add(beardLength)

        background = skin[Image.ModalDark]
        pad(PADDING)
        add(wizNameTable).center().top().padTop(PADDING)
        row()
        add(descLabel).center().expand().width(LABEL_WIDTH)
        row()
        pack()
    }

    override fun getPrefWidth(): Float = WORLD_WIDTH * 0.5f
    override fun getPrefHeight(): Float = WORLD_HEIGHT * 0.5f
}

@Scene2dDsl
inline fun <S> KWidget<S>.spellActionsDialog(
    init: SpellActionDialog.(S) -> Unit = {},
): SpellActionDialog = actor(SpellActionDialog(), init)
