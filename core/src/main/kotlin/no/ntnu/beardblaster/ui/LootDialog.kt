package no.ntnu.beardblaster.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.*
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.game.Loot
import no.ntnu.beardblaster.game.GameInstance
import no.ntnu.beardblaster.game.GameLoot
import no.ntnu.beardblaster.user.UserData
import java.util.*

private val LOG = logger<LootDialog>()

@Scene2dDsl
@ExperimentalCoroutinesApi
class LootDialog(
    private val gameInstance: GameInstance
) : Table(Scene2DSkin.defaultSkin), KTable, Observer {

    var closeBtn: Button = scene2d.textButton(Nls.quitGame(), ButtonStyle.Primary.name)

    companion object {
        const val PADDING = 50f
    }

    init {
        background = skin[Image.ModalDark]
        pad(PADDING)
        defaults().space(10f).align(Align.topRight)
        columnDefaults(0).align(Align.left).expandX()
        columnDefaults(1).align(Align.right)
        gameInstance.gameLoot.addObserver(this)
        if (gameInstance.gameLoot.getLoot().isNotEmpty()) {
            renderLoot(gameInstance.gameLoot.getLoot())
        }
    }

    private fun renderLoot(loot: List<Loot>) {
        val myId = UserData.instance.user!!.id
        val winnerId = gameInstance.winningWizard?.id

        LOG.debug { "My Id: $myId" }
        LOG.debug { "Winner Id: $winnerId" }

        val myColor: Color
        val opponentColor: Color
        val text: String

        when {
            gameInstance.isDraw -> {
                text = Nls.itsADraw()
                myColor = Color.YELLOW
                opponentColor = Color.YELLOW
            }
            myId == winnerId -> {
                text = Nls.youWin()
                myColor = Color.MAGENTA
                opponentColor = Color.CYAN
            }
            else -> {
                text = Nls.youLose()
                myColor = Color.CYAN
                opponentColor = Color.MAGENTA
            }
        }

        add(scene2d.label(text, LabelStyle.LightText.name) {
            setFontScale(2f)
        }).center().colspan(2).expandX()
        row()
        add(scene2d.label(Nls.youReceive(), LabelStyle.LightText.name) {
            setFontScale(1.5f)
        })
        row()
        loot.filter { l -> l.receiver == myId }.forEach {
            add(lootLabel(it.item, myColor).apply { wrap = true })
            add(lootLabel(determineAmountText(it), myColor))
            row()
        }
        add(scene2d.label(Nls.opponentReceived(), LabelStyle.LightText.name) {
            setFontScale(1.5f)
        })
        row()
        loot.filter { l -> l.receiver != myId }.forEach {
            add(lootLabel(it.item, opponentColor).apply { wrap = true })
            add(lootLabel(determineAmountText(it), opponentColor))
            row()
        }
        add(closeBtn).colspan(2).center().bottom()
        pack()
    }

    private fun lootLabel(text: String, color: Color): Label {
        return scene2d.label(text, LabelStyle.LightText.name) {
            setColor(color)
        }
    }

    private fun determineAmountText(loot: Loot): String {
        return if (loot.item == "Beard Length") {
            "${loot.amount} cm"
        } else {
            "x ${loot.amount}"
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        LOG.debug { "Loot received -> $o $arg" }
        if (o is GameLoot) {
            if (arg is List<*>) {
                LOG.debug { "Adding loot (render)" }
                renderLoot(arg as List<Loot>)
            }
        }
    }
}

@Scene2dDsl
@ExperimentalCoroutinesApi
inline fun <S> KWidget<S>.lootDialog(
    loot: GameInstance,
    init: LootDialog.(S) -> Unit = {},
): LootDialog = actor(LootDialog(loot), init)
