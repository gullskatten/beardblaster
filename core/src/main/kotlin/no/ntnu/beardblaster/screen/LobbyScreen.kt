package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.HEIGHT
import no.ntnu.beardblaster.WIDTH
import no.ntnu.beardblaster.assets.Atlas
import no.ntnu.beardblaster.assets.Font
import no.ntnu.beardblaster.assets.get

private val log = logger<LoginScreen>()

class LobbyScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private val skin = Skin(assets[Atlas.Game])
    private val font = assets[Font.Standard]

    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var leftTable: Table
    private lateinit var rightTable: Table

    private lateinit var codeLabel: Label
    private lateinit var infoLabel: Label

    private lateinit var startGameBtn: TextButton
    private lateinit var backBtn: Button

    private val lobbyStage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        log.debug { "LOBBY SCREEN SHOWN" }

        table = Table(skin)
        table.setBounds(0f, 0f, WIDTH, HEIGHT)

        rightTable = Table(skin)
        leftTable = Table(skin)

        Label.LabelStyle(font, Color.BLACK).also {
            heading = Label("Lobby", it)
            heading.setFontScale(2f)
            it.background = skin.getDrawable("modal_fancy_header")
            heading.setAlignment(Align.center)
        }
        Label.LabelStyle(font, Color.BLACK).also {
            codeLabel = Label("39281", it)
            codeLabel.setFontScale(1.5f)
            codeLabel.setAlignment(Align.center)

            infoLabel = Label("Share this code with a friend to start playing", it)
            infoLabel.setFontScale(1f)
            infoLabel.setAlignment(Align.center)
        }

        val buttonStyle = TextButton.TextButtonStyle()
        skin.getDrawable("button_default_pressed").also { buttonStyle.down = it }
        skin.getDrawable("button_default").also { buttonStyle.up = it }

        font.apply {
            buttonStyle.font = this
        }

        val backBtnStyle = Button.ButtonStyle()
        skin.getDrawable("modal_fancy_header_button_red_cross_left").also { backBtnStyle.down = it }
        skin.getDrawable("modal_fancy_header_button_red_cross_left").also { backBtnStyle.up = it }


        startGameBtn = TextButton("START GAME", buttonStyle)
        // startGameBtn.isDisabled = true // TODO: Disabled start button until two players in lobby (or remove start button altogether and just start automatically
        backBtn = Button(backBtnStyle)
        setBtnEventListeners()

        // Creating table
        rightTable.apply {
            this.defaults().pad(30f)
            this.background = skin.getDrawable("modal_fancy")
            this.add(heading)
            this.row()
            this.add(codeLabel).pad((30f))
            this.row()
            this.add(infoLabel)
            this.row()
            this.add(startGameBtn)
        }


        val stack = Stack()
        stack.add(backBtn)
        leftTable.apply {
            this.top()
            this.add(stack).fillY().align(Align.top).padTop(50f)
        }

        table.apply {
            this.background = skin.getDrawable("background")
            this.add(leftTable).width(91f).expandY().fillY()
            this.add(rightTable).width(WIDTH * 0.9f).fillY()
        }
        // Adding actors to the stage
        lobbyStage.addActor(table)

        Gdx.input.inputProcessor = lobbyStage
    }

    override fun update(delta: Float) {}

    override fun setBtnEventListeners() {
        startGameBtn.onClick {
            // When two players have joined the game, the host can chose to start it
            // (or alternatively just start immediately (might be simpler))
            game.setScreen<GameplayScreen>()
        }
        backBtn.onClick {
            game.setScreen<MenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        lobbyStage.act(delta)
        lobbyStage.draw()

    }
}
