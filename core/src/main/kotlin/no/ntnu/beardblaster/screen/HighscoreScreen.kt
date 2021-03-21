package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets


private val LOG = logger<HighscoreScreen>()


class HighscoreScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    private lateinit var skin: Skin
    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var closeBtn: TextButton

    private val highscoreStage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        LOG.debug { "HIGHSCORE Screen" }

        skin = Skin(Assets.assetManager.get(Assets.atlas))
        table = Table(skin)
        table.setBounds(0f, 0f, viewport.worldWidth, viewport.worldHeight)

        val standardFont = Assets.assetManager.get(Assets.standardFont)

        Label.LabelStyle(standardFont, Color.BLACK).also {
            heading = Label("Leaderbeard", it)
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

        closeBtn = TextButton("CLOSE", buttonStyle)
        setBtnEventListeners()

        // Creating table
        table.apply {
            this.defaults().pad(30f)
            this.background = skin.getDrawable("background")
            this.add(heading)
            this.row()
            this.add(closeBtn)
        }

        // Adding actors to the stage
        highscoreStage.addActor(table)

        Gdx.input.inputProcessor = highscoreStage
    }

    override fun update(delta: Float) {
    }

    override fun setBtnEventListeners() {
        closeBtn.onClick {
            game.setScreen<MenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        highscoreStage.act(delta)
        highscoreStage.draw()
    }
}
