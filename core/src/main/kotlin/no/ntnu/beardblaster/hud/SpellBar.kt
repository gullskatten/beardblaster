package no.ntnu.beardblaster.hud

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import ktx.actors.onClick
import ktx.scene2d.*
import no.ntnu.beardblaster.ElementType
import no.ntnu.beardblaster.models.Element
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.get
import java.util.*

@Scene2dDsl
class SpellBar(
    styleName: String, skin: Skin, private val selected: MutableList<Element?>
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
            val element: Element? = selected[count]
            val elementBtnSize = 200f
            val slot = scene2d.table {
                background = skin[Image.SpellBarSlot]
            }

            if (element != null) {
                val elementType = ElementType.valueOf(element.name)
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
    selected: MutableList<Element?>,
    init: SpellBar.(S) -> Unit = {}
): SpellBar = actor(SpellBar(style, skin, selected), init)

