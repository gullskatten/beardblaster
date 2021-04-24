package no.ntnu.beardblaster.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.commons.game.Loot
import no.ntnu.beardblaster.game.GameLoot
import no.ntnu.beardblaster.user.UserData
import java.util.*

private val LOG = logger<LootDialog>()

@Scene2dDsl
class LootDialog(
    loot: GameLoot
) : Table(Scene2DSkin.defaultSkin), KTable, Observer {

    private val nameLabel: Label =
        scene2d.label("Loot", LabelStyle.LightText.name) {
            setAlignment(Align.center)
            setFontScale(2f)
        }

    private val yourLootLabel: Label = scene2d.label("You receive", LabelStyle.LightText.name) {
        setAlignment(Align.center)
        setFontScale(1.5f)
        wrap = true
    }

    private val opponentLootLabel: Label =
        scene2d.label("Opponent received", LabelStyle.LightText.name) {
            setAlignment(Align.center)
            setFontScale(1.5f)
            wrap = true
        }

    var closeBtn: Button = scene2d.textButton("Quit Game", ButtonStyle.Primary.name)

    companion object {
        const val PADDING = 15f
        const val LABEL_WIDTH = 960f
    }

    init {
        loot.addObserver(this)
        if (loot.getLoot().isNotEmpty()) {
            renderLoot(loot.getLoot())
        }
    }

    fun renderLoot(loot : List<Loot>) {
        background = skin[Image.ModalDark]
        pad(PADDING)
        add(nameLabel).center().top().width(LABEL_WIDTH)
        row()
        add(yourLootLabel).center().expand().width(LABEL_WIDTH)
        row()
        loot.filter { l -> l.receiver == UserData.instance.user!!.id }.forEach {
            val descLabel: Label = scene2d.label(it.item, LabelStyle.LightText.name) {
                setAlignment(Align.center)
                setFontScale(1.5f)
                wrap = true
                color = Color.valueOf("96ecff")
            }
            val amountLabel: Label = scene2d.label(determineAmountText(it), LabelStyle.LightText.name) {
                setAlignment(Align.center)
                setFontScale(1.5f)
                wrap = true
                color = Color.valueOf("96ecff")
            }
            add(descLabel).left().expand().width(LABEL_WIDTH)
            add(amountLabel).right().expand().width(LABEL_WIDTH)
            row()
        }
        add(opponentLootLabel).center().expand().width(LABEL_WIDTH)
        row()
        loot.filter { l -> l.receiver != UserData.instance.user!!.id }.forEach {
            val descLabel: Label = scene2d.label(it.item, LabelStyle.LightText.name) {
                setAlignment(Align.center)
                setFontScale(1.5f)
                wrap = true
                color = Color.LIGHT_GRAY
            }
            val amountLabel: Label = scene2d.label(determineAmountText(it), LabelStyle.LightText.name) {
                setAlignment(Align.center)
                setFontScale(1.5f)
                wrap = true
                color = Color.LIGHT_GRAY
            }
            add(descLabel).left().expand().width(LABEL_WIDTH)
            add(amountLabel).right().expand().width(LABEL_WIDTH)
            row()
        }
        add(closeBtn).center().bottom()
        pack()
    }

    private fun determineAmountText(it: Loot): String {
        return if(it.item == "Beard Length") {
            "${it.amount}cm"
        } else {
            "x${it.amount}"
        }
    }

    override fun getPrefWidth(): Float = WORLD_WIDTH * 0.5f
    override fun getPrefHeight(): Float = WORLD_HEIGHT * 0.8f

    override fun update(p0: Observable?, p1: Any?) {
        LOG.debug { "Loot received -> $p0 $p1" }
       if(p0 is GameLoot) {
           if(p1 is List<*>) {
               LOG.debug { "Adding loot (render)" }
               renderLoot(p1 as List<Loot>)
           }
       }
    }
}

@Scene2dDsl
inline fun <S> KWidget<S>.lootDialog(
    loot: GameLoot,
    init: LootDialog.(S) -> Unit = {},
): LootDialog = actor(LootDialog(loot), init)
