package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
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

class LoginMenuScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var exitBtn: TextButton
    private lateinit var loginBtn: TextButton
    private lateinit var registerBtn: TextButton

    override fun initScreen() {
        exitBtn = scene2d.textButton(Nls.exitGame())
        loginBtn = scene2d.textButton(Nls.logIn())
        registerBtn = scene2d.textButton(Nls.register())

        val table = fullSizeTable().apply {
            background = skin[Image.Modal]
            add(headingLabel(Nls.appName())).pad(50f)
            row()
            add(loginBtn).pad(40f)
            row()
            add(registerBtn).pad(40f)
            row()
            add(exitBtn).pad(40f)
        }
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        loginBtn.onClick {
            game.setScreen<LoginScreen>()
        }
        registerBtn.onClick {
            game.setScreen<RegisterScreen>()
        }
        exitBtn.onClick {
            Gdx.app.exit()
        }
    }

    override fun update(delta: Float) {}
}
