package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
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
import no.ntnu.beardblaster.HEIGHT
import no.ntnu.beardblaster.WIDTH
import no.ntnu.beardblaster.assets.Atlas
import no.ntnu.beardblaster.assets.Font
import no.ntnu.beardblaster.assets.get

private val log = logger<TutorialScreen>()

class TutorialScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private val skin = Skin(assets[Atlas.Game])
    private val font = assets[Font.Standard]

    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var closeTutorialBtn: TextButton

    private val tutorialStage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        log.debug { "TUTORIAL Screen" }

        table = Table(skin)
        table.setBounds(0f, 0f, WIDTH, HEIGHT)

        Label.LabelStyle(font, Color.BLACK).also {
            heading = Label("Tutorial", it)
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

        closeTutorialBtn = TextButton("CLOSE", buttonStyle)
        setBtnEventListeners()

        // Creating table
        table.apply {
            this.defaults().pad(30f)
            this.background = skin.getDrawable("background")
            this.add(heading)
            this.row()
            this.add(closeTutorialBtn)
        }

        // Adding actors to the stage
        tutorialStage.addActor(table)

        Gdx.input.inputProcessor = tutorialStage
    }

    override fun update(delta: Float) {}

    override fun setBtnEventListeners() {
        closeTutorialBtn.onClick {
            game.setScreen<MenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        tutorialStage.act(delta)
        tutorialStage.draw()
    }
}
