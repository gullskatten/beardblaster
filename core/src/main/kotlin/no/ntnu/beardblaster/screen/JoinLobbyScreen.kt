package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
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
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.commons.game.GameOpponent
import no.ntnu.beardblaster.lobby.LobbyData
import no.ntnu.beardblaster.lobby.LobbyRepository
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserData
import pl.mk5.gdx.fireapp.GdxFIRAuth
import java.util.*

private val LOG = logger<JoinLobbyScreen>()

class JoinLobbyScreen(
    game: BeardBlasterGame,
    batch: Batch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), Observer {
    private lateinit var waitingLabel: Label
    private lateinit var errorLabel: Label
    private val codeInput = inputField(Nls.gameCode())
    private val submitCodeBtn = scene2d.textButton(Nls.submit())
    private val backBtn = scene2d.button(ButtonStyle.Cancel.name)

    override fun initScreen() {
        // Listen for updates on LobbyData
        LobbyData.instance.addObserver(this)

        waitingLabel = bodyLabel("")
        errorLabel = bodyLabel("")
        waitingLabel.isVisible = false
        errorLabel.isVisible = false
        val content = scene2d.table {
            defaults().pad(30f)
            background = skin[Image.Modal]
            add(headingLabel(Nls.joinGame()))
            this.row()
            this.add(codeInput).width(570f)
            this.row()
            this.add(errorLabel)
            this.add(waitingLabel)
            this.row()
            this.add(submitCodeBtn)
        }
        val table = fullSizeTable().apply {
            background = skin[Image.Background]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WORLD_WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        submitCodeBtn.onClick {
            if (codeInput.text.isNotEmpty() && codeInput.text.isNotBlank())

                if (UserData.instance.user == null || GdxFIRAuth.inst().currentUser == null) {
                    errorLabel.isVisible = true
                    errorLabel.setText("Failed to join: Try to log into BeardBlaster again.")
                    return@onClick;
                }
            // This will eventually trigger "update()"
            LobbyData.instance.joinLobbyWithCode(codeInput.text)
            waitingLabel.setText("Verifying code...")
            waitingLabel.isVisible = true
        }
        backBtn.onClick {
            KtxAsync.launch {
               if(LobbyData.instance.game != null) {
                   LobbyData.instance.leaveLobby()?.collect {
                       when (it) {
                           is State.Success -> {
                               game.setScreen<MenuScreen>()
                           }
                           is State.Failed -> {
                               errorLabel.setText("Failed to leave lobby.. Please retry.")
                               errorLabel.isVisible = true
                               if(errorLabel.text.equals("Failed to leave lobby.. Please retry.")) {
                                   // Just force quit if it fails once more.
                                   game.setScreen<MenuScreen>()
                               }
                           }
                       }
                   }
               } else {
                   game.setScreen<MenuScreen>()
               }
            }
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

    // Listens for changes on lobby -> when entering lobby and when lobby starts.
    override fun update(o: Observable?, arg: Any?) {
        if(o is LobbyData) {

            if(arg is String) {
                errorLabel.setText(arg)
                errorLabel.isVisible = true
                waitingLabel.isVisible = false

            }
            if(arg is Game) {
                if(arg.started > 0L) {
                    game.setScreen<GameplayScreen>()
                } else {
                    errorLabel.isVisible = false
                    waitingLabel.isVisible = true
                    waitingLabel.setFontScale(0.8f)
                    waitingLabel.setText("Success! Waiting for player to start the game.")
                }
            }
        }
    }
}
