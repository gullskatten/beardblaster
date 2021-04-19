package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.log.logger
import ktx.scene2d.scene2d
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.fullSizeTable
import no.ntnu.beardblaster.ui.get
import no.ntnu.beardblaster.ui.headingLabel

private val LOG = logger<TutorialScreen>()

class TutorialScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var closeBtn: TextButton

    override fun initScreen() {
        closeBtn = scene2d.textButton(Nls.close())

        setBtnEventListeners()
        val table = fullSizeTable(30f).apply {
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
}
