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
import no.ntnu.beardblaster.user.UserAuth

private val log = logger<RegisterScreen>()

class RegisterScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private val skin = Skin(assets[Atlas.Game])
    private val font = assets[Font.Standard]

    private lateinit var table: Table
    private lateinit var leftTable: Table
    private lateinit var rightTable: Table
    private lateinit var heading: Label

    private lateinit var backBtn: Button
    private lateinit var createBtn: TextButton

    private lateinit var userNameInput: TextField
    private lateinit var emailInput: TextField
    private lateinit var passwordInput: TextField
    private lateinit var rePasswordInput: TextField

    private val registrationStage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        log.debug { "REGISTRATION Screen" }
        Gdx.input.inputProcessor = registrationStage
        table = Table(skin)
        table.setBounds(0f, 0f, WIDTH, HEIGHT)
        rightTable = Table(skin)
        leftTable = Table(skin)

        Label.LabelStyle(font, Color.BLACK).also {
            heading = Label("Create Wizard", it)
            heading.setFontScale(2f)
            it.background = skin.getDrawable("modal_fancy_header")
            heading.setAlignment(Align.center)
        }

        val createUserButtonStyle = TextButton.TextButtonStyle()
        skin.getDrawable("button_default_pressed").also { createUserButtonStyle.down = it }
        skin.getDrawable("button_default").also { createUserButtonStyle.up = it }

        val backBtnStyle = Button.ButtonStyle()
        skin.getDrawable("modal_fancy_header_button_red_cross_left").also {
            backBtnStyle.down = it
            backBtnStyle.up = it
        }

        font.apply {
            createUserButtonStyle.font = this
        }
        backBtn = Button(backBtnStyle)
        createBtn = TextButton("CREATE WIZARD", createUserButtonStyle)
        setBtnEventListeners()

        val textInputStyle = TextField.TextFieldStyle()

        textInputStyle.also {
            it.background = skin.getDrawable("input_texture_dark")
            it.fontColor = Color.BROWN
            it.font = font
            it.messageFontColor = Color.GRAY
        }

        userNameInput = TextField("", textInputStyle)
        userNameInput.messageText = "Enter wizard name.."
        emailInput = TextField("", textInputStyle)
        emailInput.messageText = "Enter e-mail address.."
        passwordInput = TextField("", textInputStyle)
        passwordInput.setPasswordCharacter("*"[0])
        passwordInput.isPasswordMode = true
        passwordInput.messageText = "Enter password.."
        rePasswordInput = TextField("", textInputStyle)
        rePasswordInput.setPasswordCharacter("*"[0])
        rePasswordInput.messageText = "Re-enter password.."
        rePasswordInput.isPasswordMode = true

        rightTable.apply {
            this.defaults().pad(30f)
            this.background = skin.getDrawable("modal_fancy")
            this.add(heading)
            this.row()
            this.add(userNameInput).width(570f)
            this.row()
            this.add(emailInput).width(570f)
            this.row()
            this.add(passwordInput).width(570f)
            this.row()
            this.add(rePasswordInput).width(570f)
            this.row()
            this.add(createBtn).width(370f)
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

        registrationStage.addActor(table)
    }

    override fun update(delta: Float) {}

    override fun setBtnEventListeners() {
        createBtn.onClick {
            if (emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty() && userNameInput.text.isNotEmpty()) {
                UserAuth().createUser(emailInput.text, passwordInput.text, userNameInput.text)
            }
            // TODO: Future: Don't proceed unless signup actually successful
            game.setScreen<MenuScreen>()
        }
        backBtn.onClick {
            game.setScreen<LoginMenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        registrationStage.act(delta)
        registrationStage.draw()

    }
}
