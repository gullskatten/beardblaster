package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.AbstractScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets

private val LOG = logger<LoginMenuScreenScreen>()

class LoginMenuScreenScreen(game: BeardBlasterGame) : AbstractScreen(game) {


    private lateinit var loginImg: Sprite
    private lateinit var skin: Skin
    private lateinit var table: Table
    private lateinit var heading: Label
    override fun show() {
        LOG.debug { "LOGINMENU Screen" }

        skin = Skin(Assets.assetManager.get(Assets.atlas))
        table = Table(skin)
        table.setBounds(0f,0f, viewport.worldWidth, viewport.worldHeight)

        //fonts
        var standardFont = BitmapFont(Gdx.files.internal("font_nevis/nevis.fnt"))

        //creating buttons
        val textButtonStyle = TextButton.TextButtonStyle()
        skin.getDrawable("button_fancy_dark_short").also { textButtonStyle.up = it }
        skin.getDrawable("button_fancy_dark_short").also { textButtonStyle.down = it }
        textButtonStyle.pressedOffsetX = 4f
        textButtonStyle.pressedOffsetY = -4f
        textButtonStyle.font = standardFont
        var buttonExit = TextButton("EXIT", textButtonStyle)
        var buttonLogin = TextButton("LOGIN", textButtonStyle)
        var buttonRegister = TextButton("REGISTER", textButtonStyle)
        buttonLogin.onClick {
            game.setScreen<LoginScreen>()
        }
        buttonExit.onClick {
            Gdx.app.exit()
        }
        buttonRegister.onClick {
            game.setScreen<RegisterScreen>()
        }

        //Creating heading
        val headingStyle = Label.LabelStyle(standardFont, Color.WHITE).also {
            heading = Label("BEARDBLASTER", it)
            heading.setFontScale(2f)
        }

        //Creating table
        table.add(heading).pad(50f)
        table.row()
        table.add(buttonLogin).pad(40f)
        table.row()
        table.add(buttonRegister).pad(40f)
        table.row()
        table.add(buttonExit).pad(40f)
        /*table.debug()*/
        stage.addActor(table)


    }

    override fun update(delta: Float) {

       /* if (Gdx.input.justTouched()) {
            game.setScreen<MenuScreen>()
        }*/

    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f,0f,0f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        stage.act(delta)
        stage.draw()
        /*batch.use{
            loginImg.draw(it)
        }*/
    }



}