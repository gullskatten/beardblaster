package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserAuth

class RegisterScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera) {
    private lateinit var backBtn: Button
    private lateinit var createBtn: TextButton
    private lateinit var userNameInput: TextField
    private lateinit var emailInput: TextField
    private lateinit var passwordInput: TextField
    private lateinit var rePasswordInput: TextField

    override fun initScreen() {
        backBtn = scene2d.button(ButtonStyle.Cancel.name)
        createBtn = scene2d.textButton(Nls.createWizard())
        userNameInput = inputField(Nls.wizardName())
        emailInput = inputField(Nls.emailAddress())
        passwordInput = passwordField(Nls.password())
        rePasswordInput = passwordField(Nls.confirmPassword())

        val content = scene2d.table {
            defaults().pad(30f)
            background = skin[Image.Modal]
            add(headingLabel(Nls.createWizard()))
            row()
            add(userNameInput).width(570f)
            row()
            add(emailInput).width(570f)
            row()
            add(passwordInput).width(570f)
            row()
            add(rePasswordInput).width(570f)
            row()
            add(createBtn).center()
        }
        val table = fullSizeTable().apply {
            background = skin[Image.Background]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WORLD_WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        createBtn.onClick {
            if (emailInput.text.isNotEmpty() && passwordInput.text.isNotEmpty() && userNameInput.text.isNotEmpty()) {
                UserAuth().createUser(emailInput.text, passwordInput.text, userNameInput.text)
            }
            // TODO: Future: Don't proceed unless signup actually successful
            game.setScreen<MenuScreen>()
        }
        backBtn.onClick {
            game.setScreen<LoginMenuScreen>()
        }
    }

    override fun update(delta: Float) {}
}
