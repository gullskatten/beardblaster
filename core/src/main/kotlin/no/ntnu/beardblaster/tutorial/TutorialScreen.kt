package no.ntnu.beardblaster.tutorial

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.*
import no.ntnu.beardblaster.BaseScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.menu.MenuScreen
import no.ntnu.beardblaster.ui.Image
import no.ntnu.beardblaster.ui.LabelStyle
import no.ntnu.beardblaster.ui.get
import no.ntnu.beardblaster.ui.headingLabel

class TutorialScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var closeBtn: TextButton
    private lateinit var table: Table

    companion object {
        const val SCREEN_PADDING = 30f
    }

    override fun initComponents() {
        closeBtn = scene2d.textButton(Nls.close())
        table = scene2d.table {
            background = skin[Image.BackgroundSecondary]
            pad(SCREEN_PADDING)
            defaults().space(SCREEN_PADDING)
            setBounds(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
            columnDefaults(0).width(WORLD_WIDTH / 2)
            add(headingLabel(Nls.tutorial()))
            row()
            add(scene2d.table {
                background = skin[Image.ModalDark]
                pad(50f)
                add(scene2d.scrollPane {
                    addActor(scene2d.label(
                        Nls.tutorialGuide(),
                        LabelStyle.LightText.name
                    ) {
                        setFontScale(1f)
                        wrap = true
                    })
                }).expand().growX().align(Align.topRight)
            }).grow()
            row()
            add(closeBtn)
        }
    }

    override fun initScreen() {
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        closeBtn.onClick {
            game.setScreen<MenuScreen>()
        }
    }

    override fun update(delta: Float) {}
}
