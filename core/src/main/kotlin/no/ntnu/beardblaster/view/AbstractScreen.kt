package no.ntnu.beardblaster.view

import com.badlogic.gdx.graphics.g2d.Batch
import ktx.app.KtxScreen
import no.ntnu.beardblaster.BeardBlasterGame

abstract class AbstractScreen(
        val game: BeardBlasterGame,
        val batch: Batch = game.batch,
        )
    : KtxScreen {
    /*val stage: Stage = game.stage*/


    abstract fun update(delta: Float)
}
