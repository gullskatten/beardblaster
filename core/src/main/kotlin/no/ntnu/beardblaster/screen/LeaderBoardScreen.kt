package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.logger
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
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
    private lateinit var closeBtn: TextButton
    private var leaderBoardHandler: LeaderBoardHandler = LeaderBoardHandler()
    private lateinit var table: Table
    private lateinit var leaderBoardListTable: Table

    override fun initScreen() {
        leaderBoardListTable = scene2d.table {
        }

        leaderBoardHandler.addObserver(this)
        KtxAsync.launch {
            leaderBoardHandler.getTopTenBeards()
        }
        closeBtn = scene2d.textButton(Nls.close())

        table = fullSizeTable(30f).apply {
            background = skin[Image.Background]
            add(headingLabel(Nls.leaderBeard()))
            row()
            add(leaderBoardListTable)
            row()
            add(closeBtn)
        }

        stage.addActor(table)
    }

    override fun update(delta: Float) {}

    override fun setBtnEventListeners() {
        closeBtn.onClick {
            game.setScreen<MenuScreen>()
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        if (o is LeaderBoardHandler) {
            leaderBoardListTable.clear()
            for (score: BeardScore in o.topTen) {
                leaderBoardListTable.add(
                    bodyLabel(
                        "${score.displayName} - ${score.beardLength} cm",
                        labelStyle = LabelStyle.BodyOutlined.name
                    )
                )
                leaderBoardListTable.row()
            }
        }
    }
}
