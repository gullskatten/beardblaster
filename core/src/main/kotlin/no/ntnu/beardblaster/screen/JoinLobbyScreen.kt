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

class JoinLobbyScreen(
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

    private lateinit var codeInput: TextField

    private lateinit var submitCodeBtn: TextButton
    private lateinit var backBtn: Button

    private val joinLobbyStage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        log.debug { "JOIN LOBBY Screen" }

        table = Table(skin)
        table.setBounds(0f, 0f, WIDTH, HEIGHT)

        rightTable = Table(skin)
        leftTable = Table(skin)

        Label.LabelStyle(font, Color.BLACK).also {
            heading = Label("Join Game", it)
            heading.setFontScale(2f)
            it.background = skin.getDrawable("modal_fancy_header")
            heading.setAlignment(Align.center)
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


        submitCodeBtn = TextButton("SUBMIT", buttonStyle)
        backBtn = Button(backBtnStyle)
        setBtnEventListeners()

        val textInputStyle = TextField.TextFieldStyle()

        textInputStyle.also {
            it.background = skin.getDrawable("input_texture_dark")
            it.fontColor = Color.BROWN
            it.font = font
            it.messageFontColor = Color.GRAY
        }

        codeInput = TextField("", textInputStyle)
        codeInput.messageText = "Enter game code.."


        // Creating table
        rightTable.apply {
            this.defaults().pad(30f)
            this.background = skin.getDrawable("modal_fancy")
            this.add(heading)
            this.row()
            this.add(codeInput).width(570f)
            this.row()
            this.add(submitCodeBtn)
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
        joinLobbyStage.addActor(table)

        Gdx.input.inputProcessor = joinLobbyStage
    }

    override fun update(delta: Float) {}


    override fun setBtnEventListeners() {
        submitCodeBtn.onClick {
            // TODO: Set off handling if code is valid. If so, join lobby (go to lobby screen)
            // but if not, show feedback to user that the code is not connected to an active game
            game.setScreen<LobbyScreen>()
        }
        backBtn.onClick {
            game.setScreen<MenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        joinLobbyStage.act(delta)
        joinLobbyStage.draw()
    }
}
