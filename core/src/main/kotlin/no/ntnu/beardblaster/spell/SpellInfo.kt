package no.ntnu.beardblaster.spell

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.log.logger
import ktx.scene2d.*
import ktx.style.textButton
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.spell.Spell
import no.ntnu.beardblaster.ui.*

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
    var lockBtn: TextButton = scene2d.textButton("Lock Spell", ButtonStyle.Primary.name)

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
            nameLabel.setText(spell.spellName)
            descLabel.setText(spell.spellDescription)
        } else {
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
    fun updateButtonLabel(lockState: SpellLockState) {
        when(lockState) {
            SpellLockState.UNLOCKED -> {
                lockBtn.style = skin.textButton(ButtonStyle.Primary.name) {
                    font = skin[FontStyle.Default]
                    up = skin[Image.ButtonPrimary]
                    over = skin[Image.ButtonPrimaryHover]
                    down = skin[Image.ButtonPrimaryPressed]
                    pressedOffsetX = 4f
                    pressedOffsetY = 4f
                }
                lockBtn.label.setText("Lock Spell")
                lockBtn.isDisabled = false
            }
            SpellLockState.LOCKING -> {
                lockBtn.style = skin.textButton(ButtonStyle.Primary.name) {
                    font = skin[FontStyle.Default]
                    up = skin[Image.Button]
                    over = skin[Image.ButtonHover]
                    down = skin[Image.ButtonPressed]
                    pressedOffsetX = 4f
                    pressedOffsetY = 4f
                }
                lockBtn.label.setText("Please Wait..")
                lockBtn.isDisabled = true
            }
            SpellLockState.LOCKED -> {
                lockBtn.style = skin.textButton {
                    font = skin[FontStyle.Default]
                    up = skin[Image.Button]
                    over = skin[Image.ButtonHover]
                    down = skin[Image.ButtonPressed]
                    pressedOffsetX = 4f
                    pressedOffsetY = 4f
                }
                lockBtn.label.setText("Waiting For Opponent")
                lockBtn.isDisabled = true
            }
            SpellLockState.FAILED -> {
                lockBtn.style = skin.textButton(ButtonStyle.Cancel.name) {
                    font = skin[FontStyle.Default]
                    up = skin[Image.ButtonCancel]
                    over = skin[Image.ButtonCancelHover]
                    down = skin[Image.ButtonCancelPressed]
                    pressedOffsetX = 4f
                    pressedOffsetY = 4f
                }
                lockBtn.label.setText("Try Again")
                lockBtn.isDisabled = false
            }
        }
    }
}

@Scene2dDsl
inline fun <S> KWidget<S>.spellInfo(
    spellCasting: SpellCasting,
    style: String = defaultStyle,
    init: SpellInfo.(S) -> Unit = {},
) : SpellInfo = actor(SpellInfo(spellCasting, style), init)
