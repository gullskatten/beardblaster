package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import ktx.actors.onClick
import ktx.scene2d.*
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.HEIGHT
import no.ntnu.beardblaster.WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.ButtonStyle
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.get
import no.ntnu.beardblaster.ui.headingLabel

class LobbyScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetManager,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var codeLabel: Label
    private val infoLabel = scene2d.label(Nls.shareGameCodeMessage())
    private val startGameBtn = scene2d.textButton(Nls.startGame())
    private val backBtn = scene2d.button(ButtonStyle.Cancel.name)

    private val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        setBtnEventListeners()
        // TODO This code should be generated
        val code = "39281"
        codeLabel = scene2d.label(code) {
            setFontScale(1.5f)
        }
        val content = scene2d.table {
            defaults().pad(30f)
            background = skin[Image.Modal]
            add(headingLabel(Nls.lobby()))
            row()
            add(codeLabel)
            row()
            add(infoLabel)
            row()
            add(startGameBtn)
        }
        val table = scene2d.table {
            setBounds(0f, 0f, WIDTH, HEIGHT)
            background = skin[Image.Background]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
    }

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

    override fun update(delta: Float) {}

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)
        stage.act(delta)
        stage.draw()
    }
}
