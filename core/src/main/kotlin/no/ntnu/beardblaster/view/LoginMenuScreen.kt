package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.AbstractScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets
import no.ntnu.beardblaster.worldHeight
import no.ntnu.beardblaster.worldWidth

private val LOG = logger<LoginMenuScreenScreen>()

class LoginMenuScreenScreen(game: BeardBlasterGame) : AbstractScreen(game) {


    private lateinit var skin: Skin
    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var buttonExit: TextButton
    private lateinit var buttonLogin: TextButton
    private lateinit var buttonRegister: TextButton

    private val loginMenuStage: Stage by lazy {
        val result = Stage(FitViewport(worldWidth, worldHeight))
        Gdx.input.inputProcessor = result
        result
    }
    override fun show() {
        LOG.debug { "LOGINMENU Screen" }

        skin = Skin(Assets.assetManager.get(Assets.atlas))
        table = Table(skin)
        table.setBounds(0f,0f, viewport.worldWidth, viewport.worldHeight)

        //fonts
        var standardFont = Assets.assetManager.get(Assets.standardFont)

        //creating buttons
        val textButtonStyle = TextButton.TextButtonStyle()
        skin.getDrawable("button_default").also { textButtonStyle.up = it }
        skin.getDrawable("button_default_hover").also { textButtonStyle.over = it }
        skin.getDrawable("button_default_pressed").also { textButtonStyle.down = it }
        textButtonStyle.pressedOffsetX = 4f
        textButtonStyle.pressedOffsetY = -4f
        textButtonStyle.font = standardFont

        buttonExit = TextButton("EXIT", textButtonStyle)
        buttonLogin = TextButton("LOGIN", textButtonStyle)
        buttonRegister = TextButton("REGISTER", textButtonStyle)


        //Creating heading
        val headingStyle = Label.LabelStyle(standardFont, Color.WHITE).also {
            heading = Label("BEARDBLASTER", it)
            heading.setFontScale(2f)
        }

        //Creating table
        table.background = skin.getDrawable("modal_fancy")
        table.add(heading).pad(50f)

        table.add(buttonLogin).pad(40f)

        table.add(buttonRegister).pad(40f)
        table.row()
        table.add(buttonExit).pad(40f)
        // Adding actors to the stage
        loginMenuStage.addActor(table)


    }

    override fun update(delta: Float) {
        buttonLogin.onClick {
            game.setScreen<LoginScreen>()
        }
        buttonExit.onClick {
            Gdx.app.exit()
        }
        buttonRegister.onClick {
            game.setScreen<RegisterScreen>()
        }

    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f,0f,0f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        loginMenuStage.act(delta)
        loginMenuStage.draw()
    }



}