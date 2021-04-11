package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.scene2d
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.fullSizeTable
import no.ntnu.beardblaster.ui.get
import no.ntnu.beardblaster.ui.headingLabel

class HighScoreScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var closeBtn: TextButton

    override fun initScreen() {
        closeBtn = scene2d.textButton(Nls.close())

        val table = fullSizeTable(30f).apply {
            background = skin[Image.Background]
            add(headingLabel(Nls.leaderBeard()))
            row()
            add(closeBtn)
        }
        stage.addActor(table)
    }

    override fun update(delta: Float) {}

    override fun setBtnEventListeners() {
        closeBtn.onClick {
            game.setScreen<MenuScreen>()
        }
    }
}
