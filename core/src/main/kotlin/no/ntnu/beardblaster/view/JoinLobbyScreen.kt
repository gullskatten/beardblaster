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
import no.ntnu.beardblaster.worldHeight
import no.ntnu.beardblaster.worldWidth

private val LOG = logger<LoginScreen>()

class JoinLobbyScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    private lateinit var skin: Skin
    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var leftTable: Table
    private lateinit var rightTable: Table

    private lateinit var codeInput: TextField

    private lateinit var submitCodeBtn: TextButton
    private lateinit var backBtn: Button

    private val joinLobbyStage: Stage by lazy {
        val result = Stage(FitViewport(worldWidth, worldHeight))
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        LOG.debug { "JOIN LOBBY Screen" }

        skin = Skin(Assets.assetManager.get(Assets.atlas))
        table = Table(skin)
        table.setBounds(0f, 0f, viewport.worldWidth, viewport.worldHeight)

        rightTable = Table(skin)
        leftTable = Table(skin)


        val standardFont = Assets.assetManager.get(Assets.standardFont)

        Label.LabelStyle(standardFont, Color.BLACK).also {
            heading = Label("Join Game", it)
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

        val backBtnStyle = Button.ButtonStyle()
        skin.getDrawable("modal_fancy_header_button_red_cross_left").also { backBtnStyle.down = it }
        skin.getDrawable("modal_fancy_header_button_red_cross_left").also { backBtnStyle.up = it }


        submitCodeBtn = TextButton("SUBMIT", buttonStyle)
        backBtn = Button(backBtnStyle)
        val textInputStyle = TextField.TextFieldStyle()

        textInputStyle.also {
            it.background = skin.getDrawable("input_texture_dark")
            it.fontColor = Color.BROWN
            it.font = standardFont
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
            this.add(rightTable).width(viewport.worldWidth * 0.9f).fillY()
        }
        // Adding actors to the stage
        joinLobbyStage.addActor(table)

        Gdx.input.inputProcessor = joinLobbyStage

    }

    override fun update(delta: Float) {
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