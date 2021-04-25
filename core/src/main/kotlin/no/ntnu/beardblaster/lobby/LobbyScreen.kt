package no.ntnu.beardblaster.lobby

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import kotlinx.coroutines.InternalCoroutinesApi
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.*
import no.ntnu.beardblaster.BaseScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.leaderboard.BeardScale
import no.ntnu.beardblaster.ui.*

class LobbyScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), LobbyPresenter.View {
    private val presenter = LobbyPresenter(this, game)
    private lateinit var codeLabel: Label
    private lateinit var opponentLabel: Label
    private lateinit var opponentBeardLabel: Label
    private lateinit var infoLabel: Label
    private lateinit var startGameBtn: TextButton
    private lateinit var backBtn: Button

    override fun initScreen() {
        codeLabel = bodyLabel("Creating game..", 1.5f, LabelStyle.BodyOutlined.name)
        opponentLabel = bodyLabel("Waiting for opponent to join..", 1.5f, LabelStyle.Body.name)
        opponentBeardLabel = bodyLabel("", 1.5f, LabelStyle.Body.name)

        presenter.init()

        infoLabel = scene2d.label(Nls.shareGameCodeMessage())
        startGameBtn = scene2d.textButton(Nls.startGame())
        backBtn = scene2d.button(ButtonStyle.Cancel.name)

        // This will only be visible when wizard B joins the game
        startGameBtn.isVisible = false

        val wizNameTable = scene2d.table {
            defaults().space(25f)
            add(opponentLabel).left()
            add(opponentBeardLabel).right()
            background = dimmedLabelBackground()
        }
        val content = scene2d.table {
            defaults().pad(30f)
            background = skin[Image.Modal]
            add(headingLabel(Nls.lobby()))
            row()
            add(codeLabel)
            row()
            add(infoLabel)
            row()
            add(wizNameTable)
            row()
            add(startGameBtn)
        }
        val table = fullSizeTable().apply {
            background = skin[Image.BackgroundSecondary]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WORLD_WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
    }

    @InternalCoroutinesApi
    override fun setBtnEventListeners() {
        startGameBtn.onClick {
            presenter.onStartGameBtnClick()
        }
        backBtn.onClick {
            presenter.onBackBtnClick()
        }
    }

    override fun dispose() {
        super.dispose()
        presenter.dispose()
    }

    override fun setInfoLabel(string: String) {
        infoLabel.setText(string)
    }

    override fun setCodeLabel(code: String) {
        codeLabel.setText(code)
    }

    override fun setOpponentLabel(string: String) {
        opponentLabel.setText(string)
    }

    override fun setOpponentBeardLengthLabel(beardLength: Float) {
        opponentBeardLabel.setText("${beardLength}cm")
        opponentBeardLabel.color = BeardScale.getBeardColor(beardLength)
    }

    override fun setStartGameBtnVisibility(isVisible: Boolean) {
        startGameBtn.isVisible = isVisible
    }
}
