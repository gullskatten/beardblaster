package no.ntnu.beardblaster.hud

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.actors.onClick
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*
import no.ntnu.beardblaster.ElementType
import no.ntnu.beardblaster.commons.spell.Element
import no.ntnu.beardblaster.models.SpellCasting
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.get
import java.util.*

private val LOG = logger<SpellBar>()

@Scene2dDsl
class SpellBar(
    styleName: String, skin: Skin, private val spellCasting: SpellCasting
) : Table(skin), KTable, Observer {
    companion object {
        const val MAX_SLOTS = 3
    }

    init {
        ElementChangedObserver.instance.addObserver(this)
        build()
    }

    fun update() {
        this.clear()
        build()
    }

    private fun build() {
        for (count in 0 until MAX_SLOTS) {
            val element: Element? = spellCasting.selectedElements[count]
            val elementBtnSize = 200f
            val slot = scene2d.table {
                background = skin[Image.SpellBarSlot]
            }

            if (element != null) {
                val elementType = ElementType.valueOf(element.elementName)
                val elementButton = scene2d.button(elementType.name) {
                    this.setPosition((7 + count * 57).toFloat(), 8f)
                }
                elementButton.onClick {
                    ElementClickedObserver.instance.notify(count)
                }
                slot.add(elementButton).width(elementBtnSize).height(elementBtnSize)
            }
            this.add(slot).height(elementBtnSize).width(elementBtnSize)
        }
    }

    override fun update(p0: Observable?, p1: Any?) {
        LOG.debug { "Update spell bar" }
        this.update()
    }
}

// Adding a factory method for the custom widget:
/**
 * Adds DSL that creates [SpellBar].
 */
@Scene2dDsl
inline fun <S> KWidget<S>.spellbar(
    skin: Skin = Scene2DSkin.defaultSkin,
    style: String = defaultStyle,
    spellCasting: SpellCasting,
    init: SpellBar.(S) -> Unit = {}
): SpellBar = actor(SpellBar(style, skin, spellCasting), init)

