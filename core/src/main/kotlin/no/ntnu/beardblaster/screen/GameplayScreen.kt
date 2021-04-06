package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.log.info
import ktx.log.logger
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.HEIGHT
import no.ntnu.beardblaster.WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.headingLabel

private val log = logger<GameplayScreen>()

class GameplayScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private val attackBtn = scene2d.textButton(Nls.attack())
    private val quiteBtn = scene2d.textButton(Nls.quit())

    private val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        setBtnEventListeners()
        val table = scene2d.table {
            setBounds(0f, 0f, WIDTH, HEIGHT)
            add(headingLabel(Nls.preparationPhase())).pad(50f)
            row()
            add(attackBtn).pad(40f)
            row()
            add(quiteBtn).pad(40f)
            row()
        }
        stage.clear()
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
    }

    override fun setBtnEventListeners() {
        attackBtn.onClick {
            log.info { Nls.wizardNAttacks(1) }
        }
        quiteBtn.onClick {
            game.removeScreen<GameplayScreen>()
            // FIXME Really needed to add a new game play screen here?
            // Might be better to add it when joining/starting a new game.
            game.addScreen(GameplayScreen(game, batch, assets, camera))
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
