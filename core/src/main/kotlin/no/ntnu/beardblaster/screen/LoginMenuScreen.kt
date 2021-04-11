package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.log.debug
import ktx.log.logger
import ktx.scene2d.scene2d
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.*

private val log = logger<LoginMenuScreen>()

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
            InputDialog(Nls.login()).apply {
                input("email", Nls.emailAddress())
                input("password", Nls.password(), true)
                okBtn.onChange {
                    log.debug { "Got data from login dialog: $data" }
                    hide()
                    // TODO Actually try to login the user
                    game.setScreen<MenuScreen>()
                }
                cancelBtn.onChange { hide() }
            }.show(stage)
        }
        registerBtn.onClick {
            InputDialog(Nls.register()).apply {
                input("username", Nls.wizardName())
                input("email", Nls.emailAddress())
                input("password", Nls.password(), true)
                okBtn.onChange {
                    log.debug { "Got data from register dialog: $data" }
                    hide()
                    // TODO Actually register the user
                    game.setScreen<MenuScreen>()
                }
                cancelBtn.onChange { hide() }
            }.show(stage)
        }
        exitBtn.onClick {
            Gdx.app.exit()
        }
    }

    override fun update(delta: Float) {}
}
