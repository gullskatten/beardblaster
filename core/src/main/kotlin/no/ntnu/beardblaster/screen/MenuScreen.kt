package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.scene2d
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.fullSizeTable
import no.ntnu.beardblaster.ui.get
import no.ntnu.beardblaster.ui.headingLabel
import no.ntnu.beardblaster.user.UserAuth

class MenuScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var createGameBtn: TextButton
    private lateinit var joinGameBtn: TextButton
    private lateinit var highScoreBtn: TextButton
    private lateinit var tutorialBtn: TextButton
    private lateinit var logoutBtn: TextButton
    private lateinit var exitBtn: TextButton

    override fun initScreen() {
        createGameBtn = scene2d.textButton(Nls.createGame())
        joinGameBtn = scene2d.textButton(Nls.joinGame())
        highScoreBtn = scene2d.textButton(Nls.leaderBeard())
        tutorialBtn = scene2d.textButton(Nls.tutorial())
        logoutBtn = scene2d.textButton(Nls.logOut())
        exitBtn = scene2d.textButton(Nls.exitGame())

        val table = fullSizeTable(20f).apply {
            background = skin[Image.Background]
            add(headingLabel(Nls.welcomeWizard())).colspan(4).center()
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

    override fun update(delta: Float) {}
}
