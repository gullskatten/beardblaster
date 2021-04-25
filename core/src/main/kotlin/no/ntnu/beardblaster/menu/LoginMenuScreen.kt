package no.ntnu.beardblaster.menu

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.scene2d
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BaseScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.LoginDialog
import no.ntnu.beardblaster.user.RegisterDialog

class LoginMenuScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera
) : BaseScreen(game, batch, assets, camera), LoginMenuPresenter.View {
    private val presenter = LoginMenuPresenter(this, game)
    private lateinit var exitBtn: TextButton
    private lateinit var loginBtn: TextButton
    private lateinit var registerBtn: TextButton
    private val errorLabel = bodyLabel("", 1.5f, LabelStyle.Error.name)

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
            row()
            add(errorLabel)
        }
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        loginBtn.onClick { showLoginDialog() }
        registerBtn.onClick { showRegisterDialog() }
        exitBtn.onClick { presenter.onExitBtnClick() }
    }

    private fun showLoginDialog() {
        LoginDialog().apply {
            okBtn.onChange {
                presenter.onLoginClick(isValid, email, password)
            }
        }.show(stage)
    }

    private fun showRegisterDialog() {
        RegisterDialog().apply {
            okBtn.onChange {
                presenter.onRegisterClick(username, email, password)
            }
        }.show(stage)
    }

    override fun setErrorLabel(msg: String) {
        errorLabel.setText(msg)
    }
}
