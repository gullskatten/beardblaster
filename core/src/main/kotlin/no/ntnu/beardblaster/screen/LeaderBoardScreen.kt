package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.scene2d
import ktx.scene2d.scrollPane
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.leaderboard.BeardScore
import no.ntnu.beardblaster.leaderboard.LeaderBoardHandler
import no.ntnu.beardblaster.ui.*
import java.util.*

private val LOG = logger<LeaderBoardScreen>()

class LeaderBoardScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), Observer {
    private val leaderBoardHandler = LeaderBoardHandler()
    private lateinit var table: Table
    private lateinit var backBtn: TextButton
    private val leaderBoard = LeaderBoard()
    private val beardScale = BeardScale()

    companion object {
        const val SCREEN_PADDING = 30f
    }

    override fun initComponents() {
        backBtn = scene2d.textButton(Nls.back()) {
            setPosition(SCREEN_PADDING, WORLD_HEIGHT - SCREEN_PADDING - height)
        }
        table = scene2d.table {
            background = skin[Image.Background]
            pad(SCREEN_PADDING)
            defaults().space(SCREEN_PADDING)
            setBounds(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
            columnDefaults(0).width(WORLD_WIDTH / 2)
            add(headingLabel(Nls.leaderBeard()))
            row()
            add(scene2d.table {
                background = skin[Image.ModalDark]
                pad(50f)
                add(scene2d.scrollPane {
                    addActor(leaderBoard)
                }).expand().growX().align(Align.topRight)
            }).grow()
            row()
            add(beardScale)
        }
    }

    override fun initScreen() {
        leaderBoardHandler.addObserver(this)
        KtxAsync.launch {
            LOG.debug { "Summoning the beards…" }
            leaderBoardHandler.getTopTenBeards()
        }
        stage.addActor(table)
        stage.addActor(backBtn)
    }

    override fun setBtnEventListeners() {
        backBtn.onClick {
            game.setScreen<MenuScreen>()
        }
    }

    override fun update(delta: Float) {}

    override fun update(o: Observable, arg: Any?) {
        if (o is LeaderBoardHandler) {
            leaderBoard.clear()
            for (score: BeardScore in o.topTen) {
                leaderBoard.addScore(score)
            }
        }
    }
}
