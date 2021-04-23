package no.ntnu.beardblaster.ui

import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.spell.Spell
import no.ntnu.beardblaster.hud.ElementChangedObserver
import no.ntnu.beardblaster.models.SpellCasting

private val log = logger<SpellInfo>()

@Scene2dDsl
class SpellInfo (
    private val spellCasting: SpellCasting,
    style: String,
) : Table(Scene2DSkin.defaultSkin), KTable {

    private val nameLabel: Label = scene2d.label("", LabelStyle.LightText.name) {
        setAlignment(Align.center)
        setFontScale(2f)
    }
    private val descLabel: Label = scene2d.label("", LabelStyle.LightText.name) {
        setAlignment(Align.center)
        setFontScale(1.5f)
        wrap = true
    }
    var lockBtn: Button = scene2d.textButton("Lock spell", ButtonStyle.Primary.name)

    companion object {
        const val PADDING = 20f
        const val LABEL_WIDTH = 860f
    }

    init {
        background = skin[Image.ModalDark]
        pad(PADDING)
        add(nameLabel).center().top().padTop(PADDING).width(LABEL_WIDTH)
        row()
        add(descLabel).center().expand().width(LABEL_WIDTH)
        row()
        add(lockBtn).center().bottom()
        pack()
        ElementChangedObserver.instance.addObserver { _, _ -> update() }
        update()
    }

    private fun update() {
        val spell: Spell? = spellCasting.getSelectedSpell()
        if (spell != null) {
            log.debug { "Spell (${spell.spellName}, ${spell.spellDescription})" }
            nameLabel.setText(spell.spellName)
            descLabel.setText(spell.spellDescription)
        } else {
            log.debug { "Spell is not completed" }
            val maxSlots = spellCasting.selectedElements.count()
            val selected = spellCasting.selectedElements.filterNotNull().count()
            val message = when(maxSlots - selected) {
                1 -> Nls.selectOneMoreElement()
                in 2 until maxSlots -> Nls.selectMoreElements(maxSlots - selected)
                else -> Nls.selectMaxElement(maxSlots)
            }
            descLabel.setText(message)
        }
        nameLabel.isVisible = spell != null
        lockBtn.isVisible = spell != null
    }

    override fun getPrefWidth(): Float = WORLD_WIDTH * 0.5f
    override fun getPrefHeight(): Float = WORLD_HEIGHT * 0.5f
}

@Scene2dDsl
inline fun <S> KWidget<S>.spellInfo(
    spellCasting: SpellCasting,
    style: String = defaultStyle,
    init: SpellInfo.(S) -> Unit = {},
) : SpellInfo = actor(SpellInfo(spellCasting, style), init)
