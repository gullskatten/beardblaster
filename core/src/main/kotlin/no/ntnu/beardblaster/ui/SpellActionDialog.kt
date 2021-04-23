package no.ntnu.beardblaster.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.log.logger
import ktx.scene2d.*
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH

private val log = logger<SpellActionDialog>()

@Scene2dDsl
class SpellActionDialog(
) : Table(Scene2DSkin.defaultSkin), KTable {

    private val nameLabel: Label =
        scene2d.label("Calculating Spells", LabelStyle.LightText.name) {
            setAlignment(Align.center)
            setFontScale(2f)
        }
    private val descLabel: Label = scene2d.label("Hold on...", LabelStyle.LightText.name) {
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

    fun updateDescLabelText(description: String) {
        descLabel.setText(description)
    }

    init {
        background = skin[Image.ModalDark]
        pad(PADDING)
        add(nameLabel).center().top().padTop(PADDING).width(LABEL_WIDTH)
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
