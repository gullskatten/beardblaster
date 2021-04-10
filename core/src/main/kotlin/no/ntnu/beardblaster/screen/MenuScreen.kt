package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.info
import ktx.log.logger
import ktx.scene2d.scene2d
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserAuth
import no.ntnu.beardblaster.user.UserData
import java.util.*

private val LOG = logger<MenuScreen>()

class MenuScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), Observer {
    private val createGameBtn = scene2d.textButton(Nls.createGame())
    private val joinGameBtn = scene2d.textButton(Nls.joinGame())
    private val highScoreBtn = scene2d.textButton(Nls.leaderBeard())
    private val tutorialBtn = scene2d.textButton(Nls.tutorial())
    private val logoutBtn = scene2d.textButton(Nls.logOut())
    private val exitBtn = scene2d.textButton(Nls.exitGame())
    private val currentWizardLabel = bodyLabel("")
    private val wizardHeading = headingLabel("BeardBlaster")

    override fun initScreen() {
        KtxAsync.launch {
            UserData.instance.loadUserData()
        }
        UserData.instance.addObserver(this)

        val table = fullSizeTable(20f).apply {
            background = skin[Image.Background]
            add(wizardHeading).colspan(4).center()
            row()
            add(currentWizardLabel).colspan(4).center()
            row()
            add(createGameBtn).colspan(4).center()
            row()
            add(joinGameBtn).colspan(4).center()
            row()
            add(highScoreBtn).colspan(2).center()
            add(tutorialBtn).colspan(2).center()
            row()
            add(logoutBtn).colspan(2).center()
            add(exitBtn).colspan(2).center()
        }
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        createGameBtn.onClick {
            // Handle creation of game, and then go to Lobby screen to display code and wait for player 2
            game.setScreen<LobbyScreen>()
        }
        joinGameBtn.onClick {
            game.setScreen<JoinLobbyScreen>()
        }
        highScoreBtn.onClick {
            game.setScreen<HighScoreScreen>()
        }
        tutorialBtn.onClick {
            game.setScreen<TutorialScreen>()
        }
        logoutBtn.onClick {
            if (UserAuth().isLoggedIn()) {
                UserAuth().signOut()
            }
            game.setScreen<LoginMenuScreen>()
        }
        exitBtn.onClick {
            Gdx.app.exit()
        }
    }

    override fun update(delta: Float) {

    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
    }

    override fun update(p0: Observable?, p1: Any?) {
        LOG.info { p1.toString() }
        currentWizardLabel.setText(p1.toString())
    }

    override fun dispose() {
        super.dispose()
        UserData.instance.deleteObserver(this)
    }
}
