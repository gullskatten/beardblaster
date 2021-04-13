package no.ntnu.beardblaster.screen

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.log.error
import ktx.log.info
import ktx.log.logger
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.User
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserAuth
import no.ntnu.beardblaster.user.UserRepository
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser

val LOG = logger<RegisterScreen>()

class RegisterScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
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
                UserAuth().createUser(emailInput.text, passwordInput.text, userNameInput.text).then<GdxFirebaseUser> {

                    val user = User(displayName = userNameInput.text, id = it.userInfo.uid)

                    KtxAsync.launch {
                        UserRepository().create(user, "users").collect {
                            when(it) {
                                is State.Success -> {
                                    game.setScreen<MenuScreen>()
                                }
                                is State.Failed -> {
                                    LOG.error { it.message }
                                }
                                is State.Loading -> {
                                    LOG.info { "Creating user.."}
                                }
                            }
                        }
                    }

                }.fail { s, _ ->
                        if (s.contains("The email address is already in use by another account")) {
                            // TODO: Show error: User already exist
                        }
                        LOG.error { s }
                        // TODO: SHOW ERROR TO USER (ON USER CREATION FAIL)
                    }
            }
        }
        backBtn.onClick {
            game.setScreen<LoginMenuScreen>()
        }
    }

    override fun update(delta: Float) {}
}
