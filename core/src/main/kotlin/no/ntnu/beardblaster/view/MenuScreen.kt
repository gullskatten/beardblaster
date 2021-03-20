package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.AbstractScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets
import no.ntnu.beardblaster.user.UserAuth
import no.ntnu.beardblaster.worldHeight
import no.ntnu.beardblaster.worldWidth


private val LOG = logger<MenuScreen>()


class MenuScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    private lateinit var skin: Skin
    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var createGameBtn: TextButton
    private lateinit var joinGameBtn: TextButton
    private lateinit var highscoreBtn: TextButton
    private lateinit var tutorialBtn: TextButton
    private lateinit var logoutBtn: TextButton
    private lateinit var exitBtn: TextButton

    private val menuStage: Stage by lazy {
        val result = Stage(FitViewport(worldWidth, worldHeight))
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        LOG.debug { "MENU Screen" }

        skin = Skin(Assets.assetManager.get(Assets.atlas))
        table = Table(skin)
        table.setBounds(0f, 0f, viewport.worldWidth, viewport.worldHeight)

        val standardFont = Assets.assetManager.get(Assets.standardFont)

        Label.LabelStyle(standardFont, Color.BLACK).also {
            heading = Label("Welcome Wizard", it)
            heading.setFontScale(2f)
            it.background = skin.getDrawable("modal_fancy_header")
            heading.setAlignment(Align.center)
        }

        val buttonStyle = TextButton.TextButtonStyle()
        skin.getDrawable("button_default_pressed").also { buttonStyle.down = it }
        skin.getDrawable("button_default").also { buttonStyle.up = it }

        standardFont.apply {
            buttonStyle.font = this
        }

        createGameBtn = TextButton("CREATE GAME", buttonStyle)
        joinGameBtn = TextButton("JOIN GAME", buttonStyle)
        highscoreBtn = TextButton("LEADERBEARD", buttonStyle)
        tutorialBtn = TextButton("TUTORIAL", buttonStyle)
        logoutBtn = TextButton("LOGOUT", buttonStyle)
        exitBtn = TextButton("EXIT GAME", buttonStyle)
        setBtnEventListeners()

        val textInputStyle = TextField.TextFieldStyle()

        textInputStyle.also {
            it.background = skin.getDrawable("input_texture_dark")
            it.fontColor = Color.BROWN
            it.font = standardFont
            it.messageFontColor = Color.GRAY
        }

        table.apply {
            this.defaults().pad(20f)
            this.background = skin.getDrawable("background")
            this.add(heading).colspan(4).center()
            this.row()
            this.add(createGameBtn).colspan(4).center()
            this.row()
            this.add(joinGameBtn).colspan(4).center()
            this.row()
            this.add(highscoreBtn).colspan(2).center()
            this.add(tutorialBtn).colspan(2).center()
            this.row()
            this.add(logoutBtn).colspan(2).center()
            this.add(exitBtn).colspan(2).center()
        }

        // Adding actors to the stage
        menuStage.addActor(table)
        Gdx.input.inputProcessor = menuStage
    }

    override fun update(delta: Float) {

    }

    override fun setBtnEventListeners() {
        createGameBtn.onClick {
            // Handle creation of game, and then go to Lobby screen to display code and wait for player 2
            game.setScreen<LobbyScreen>()
        }
        joinGameBtn.onClick {
            game.setScreen<JoinLobbyScreen>()
        }
        highscoreBtn.onClick {
            game.setScreen<HighscoreScreen>()
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

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        menuStage.act(delta)
        menuStage.draw()
    }
}
