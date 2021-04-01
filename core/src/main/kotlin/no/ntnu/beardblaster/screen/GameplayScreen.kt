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

private val LOG = logger<GameplayScreen>()

class GameplayScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var btnAttack: TextButton
    private lateinit var btnQuit: TextButton

    private val skin = Skin(assets[Atlas.Game])
    private val font = assets[Font.Standard]

    private val gameplayStage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        LOG.debug { "GAMEPLAY Screen" }

        table = Table(skin)
        table.setBounds(0f, 0f, WIDTH, HEIGHT)

        // Creating buttons
        val textButtonStyle = TextButton.TextButtonStyle()
        skin.getDrawable("button_default").also { textButtonStyle.up = it }
        skin.getDrawable("button_default_hover").also { textButtonStyle.over = it }
        skin.getDrawable("button_default_pressed").also { textButtonStyle.down = it }
        textButtonStyle.pressedOffsetX = 4f
        textButtonStyle.pressedOffsetY = -4f
        textButtonStyle.font = font

        btnAttack = TextButton("ATTACK", textButtonStyle)
        btnQuit = TextButton("QUIT", textButtonStyle)
        setBtnEventListeners()

        Label.LabelStyle(font, Color.BLACK).also {
            heading = Label("Preparation phase", it)
            heading.setFontScale(2f)
            it.background = skin.getDrawable("modal_fancy_header")
            heading.setAlignment(Align.center)
        }

        // Creating table
        table.add(heading).pad(50f)
        table.row()
        table.add(btnAttack).pad(40f)
        table.row()
        table.add(btnQuit).pad(40f)
        table.row()

        // Adding actors to the stage
        gameplayStage.addActor(table)

        Gdx.input.inputProcessor = gameplayStage
    }

    override fun update(delta: Float) {}

    override fun setBtnEventListeners() {
        btnAttack.onClick {
            LOG.debug { "Wizard 1 attacks" }
        }
        btnQuit.onClick {
            game.removeScreen<GameplayScreen>()
            // XXX Really needed to add a new game play screen here?
            //game.addScreen(GameplayScreen(game, batch, assets, camera))
            game.setScreen<MenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        gameplayStage.act(delta)
        gameplayStage.draw()
    }
}
