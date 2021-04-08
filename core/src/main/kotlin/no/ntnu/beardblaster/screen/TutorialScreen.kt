package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Stage
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.get
import no.ntnu.beardblaster.ui.headingLabel

class TutorialScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private val closeBtn = scene2d.textButton(Nls.close())

    private val stage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        setBtnEventListeners()
        val table = scene2d.table {
            setBounds(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
            defaults().pad(30f)
            background = skin[Image.Background]
            add(headingLabel(Nls.tutorial()))
            row()
            add(closeBtn)
        }
        stage.addActor(table)
        Gdx.input.inputProcessor = stage
    }

    override fun setBtnEventListeners() {
        closeBtn.onClick {
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
