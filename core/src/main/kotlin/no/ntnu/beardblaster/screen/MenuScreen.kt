package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Label
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
import no.ntnu.beardblaster.commons.user.User
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserAuth
import no.ntnu.beardblaster.user.UserData
import java.util.*

private val LOG = logger<MenuScreen>()

class MenuScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), Observer {
    private lateinit var createGameBtn: TextButton
    private lateinit var joinGameBtn: TextButton
    private lateinit var leaderBoardBtn: TextButton
    private lateinit var tutorialBtn: TextButton
    private lateinit var logoutBtn: TextButton
    private lateinit var exitBtn: TextButton
    private lateinit var wizardHeading: Label

    private val currentWizardLabel: Label =
        bodyLabel(
            UserData.instance.getCurrentUserString(),
            1.5f,
            LabelStyle.Body.name
        )
    private val currentWizardBeardLabel: Label = bodyLabel(
        "",
        1.5f,
        LabelStyle.Body.name
    )

    override fun initScreen() {
        createGameBtn = scene2d.textButton(Nls.createGame())
        joinGameBtn = scene2d.textButton(Nls.joinGame())
        leaderBoardBtn = scene2d.textButton(Nls.leaderBeard())
        tutorialBtn = scene2d.textButton(Nls.tutorial())
        logoutBtn = scene2d.textButton(Nls.logOut())
        exitBtn = scene2d.textButton(Nls.exitGame())
        wizardHeading = headingLabel("BeardBlaster")

        val wizNameTable = scene2d.table {
            defaults().space(25f)
            add(currentWizardLabel).left()
            add(currentWizardBeardLabel).right()
            background = dimmedLabelBackground()
        }

        val table = fullSizeTable(20f).apply {
            background = skin[Image.BackgroundSecondary]
            add(wizardHeading).colspan(4).center()
            row()
            add(wizNameTable).colspan(4).center()
            row()
            add(createGameBtn).colspan(4).center()
            row()
            add(joinGameBtn).colspan(4).center()
            row()
            add(leaderBoardBtn).colspan(2).center()
            add(tutorialBtn).colspan(2).center()
            row()
            add(logoutBtn).colspan(2).center()
            add(exitBtn).colspan(2).center()
        }
        stage.addActor(table)

        if (UserData.instance.user == null && !UserData.instance.isLoading) {
            KtxAsync.launch {
                UserData.instance.loadUserData()
            }
            UserData.instance.addObserver(this)
        }
    }

    override fun setBtnEventListeners() {
        createGameBtn.onClick {
            // Handle creation of game, and then go to Lobby screen to display code and wait for player 2
            if (UserData.instance.user != null) {
                game.setScreen<LobbyScreen>()
            }
        }
        joinGameBtn.onClick {
            if (UserData.instance.user != null) {
                game.setScreen<JoinLobbyScreen>()
            }
        }
        leaderBoardBtn.onClick {
            if (UserData.instance.user != null) {
                game.setScreen<LeaderBoardScreen>()
            }
        }
        tutorialBtn.onClick {
            game.setScreen<TutorialScreen>()
        }
        logoutBtn.onClick {
            if (UserAuth().isLoggedIn()) {
                UserData.instance.setUserData(null)
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

    override fun update(p0: Observable?, p1: Any?) {
        if (p0 is UserData) {
            if (p1 is String) {
                currentWizardLabel.setText(p1.toString())
            } else if (p1 is User) {
                currentWizardLabel.setText(p1.displayName)
                currentWizardBeardLabel.setText("${p1.beardLength}cm")
                currentWizardBeardLabel.color = BeardScale.getBeardColor(p1.beardLength)
            }
        }
    }

    override fun dispose() {
        super.dispose()
        UserData.instance.deleteObserver(this)
    }
}
