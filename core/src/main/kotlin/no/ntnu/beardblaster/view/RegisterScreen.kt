package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.AbstractScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets
import no.ntnu.beardblaster.worldHeight
import no.ntnu.beardblaster.worldWidth

private val LOG = logger<RegisterScreen>()
class RegisterScreen(game: BeardBlasterGame) : AbstractScreen(game) {

    private val registrationStage: Stage by lazy {
        val result = Stage(FitViewport(worldWidth, worldHeight))
        Gdx.input.inputProcessor = result
        result
    }


    private lateinit var skin: Skin
    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var checkButton: TextButton
    private lateinit var unCheckButton: TextButton
    private lateinit var createButton: TextButton

    private lateinit var userNameInput: TextField
    private lateinit var emailInput: TextField
    private lateinit var passwordIput: TextField
    private lateinit var rePasswordIput: TextField
    override fun show() {
        LOG.debug { "Registration Screen Shown" }

        skin = Skin(Assets.assetManager.get(Assets.atlas))
        table = Table(skin)
        table.setBounds(0f,0f, viewport.worldWidth, viewport.worldHeight)


        var standardFont = Assets.assetManager.get(Assets.standardFont)


        val headingStyle = Label.LabelStyle(standardFont, Color.BLACK).also {
            heading = Label("Create Wizard", it)
            heading.setFontScale(2f)
            it.background = skin.getDrawable("modal_fancy_header")
            heading.setAlignment(Align.center)
        }

        val checkButtonStyle = TextButton.TextButtonStyle()
        val unCheckButtonStyle = TextButton.TextButtonStyle()
        val createUserButtonStyle = TextButton.TextButtonStyle()
        skin.getDrawable("modal_fancy_header_button_green").also { checkButtonStyle.up = it }
        skin.getDrawable("modal_fancy_header_button_green_check").also { checkButtonStyle.down = it }
        skin.getDrawable("modal_fancy_header_button_red").also { unCheckButtonStyle.up = it }
        skin.getDrawable("modal_fancy_header_button_red_cross").also { unCheckButtonStyle.down = it }
        skin.getDrawable("button_fancy_dark_short").also { createUserButtonStyle.down = it }
        skin.getDrawable("button_fancy_dark_short").also { createUserButtonStyle.up = it }

        standardFont.apply {
            checkButtonStyle.font = this
            unCheckButtonStyle.font = this
            createUserButtonStyle.font = this
        }

        checkButton = TextButton("", checkButtonStyle)
        unCheckButton = TextButton("", unCheckButtonStyle)
        createButton = TextButton("Create Wizard", createUserButtonStyle)

        val textInputStyle = TextField.TextFieldStyle()

        textInputStyle.also {
            it.background = skin.getDrawable("input_texture_dark")
            it.fontColor = Color.BLACK
            it.font = standardFont
        }

        userNameInput = TextField("Enter wizard name..", textInputStyle)
        emailInput = TextField("Enter e-mail address..", textInputStyle)
        passwordIput = TextField("Enter password..", textInputStyle)
        rePasswordIput = TextField("Re enter password..", textInputStyle)


        //%TODO(find out why input fields renders with wrong width)
        //Creating table
        table.defaults().pad(30f)
        table.background = skin.getDrawable("bg_modal_fancy")
        table.add(heading)
        table.row()
        table.add(userNameInput).width(570f)
        table.row()
        table.add(emailInput).width(570f)
        table.row()
        table.add(passwordIput).width(570f)
        table.row()
        table.add(rePasswordIput).width(570f)
        table.row()
        table.add(createButton)
        // Adding actors to the stage
        registrationStage.addActor(table)
    }

    override fun update(delta: Float) {
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f,0f,0f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        registrationStage.act(delta)
        registrationStage.draw()

    }

    override fun resize(width: Int, height: Int) {

    }

    override fun dispose() {


    }
}
