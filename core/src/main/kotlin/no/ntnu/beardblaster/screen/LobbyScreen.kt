package no.ntnu.beardblaster.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.scene2d.*
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.game.Game
import no.ntnu.beardblaster.lobby.LobbyData
import no.ntnu.beardblaster.lobby.LobbyRepository
import no.ntnu.beardblaster.ui.*
import java.util.*

class LobbyScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), Observer {
    private lateinit var codeLabel: Label
    private lateinit var opponentLabel: Label
    private lateinit var infoLabel: Label
    private lateinit var startGameBtn: TextButton
    private lateinit var backBtn: Button

    override fun initScreen() {
        LobbyData.instance.addObserver(this)

        codeLabel = bodyLabel("Creating game..")
        opponentLabel = bodyLabel("Waiting for opponent to join..")

        KtxAsync.launch {
            LobbyData.instance.createLobby()
        }
        infoLabel = scene2d.label(Nls.shareGameCodeMessage())
        startGameBtn = scene2d.textButton(Nls.startGame())
        backBtn = scene2d.button(ButtonStyle.Cancel.name)

        // This will only be visible when wizard B joins the game
        startGameBtn.isVisible = false

        val content = scene2d.table {
            defaults().pad(30f)
            background = skin[Image.Modal]
            add(headingLabel(Nls.lobby()))
            row()
            add(codeLabel)
            row()
            add(infoLabel)
            row()
            add(opponentLabel)
            row()
            add(startGameBtn)
        }
        val table = fullSizeTable().apply {
            background = skin[Image.Background]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WORLD_WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
    }

    @InternalCoroutinesApi
    override fun setBtnEventListeners() {
        startGameBtn.onClick {
            // When two players have joined the game, the host can chose to start it
            // (or alternatively just start immediately (might be simpler))
            KtxAsync.launch {
                LobbyData.instance.startGame()?.collect {
                    when (it) {
                        is State.Loading -> {
                            opponentLabel.setText("Starting game..")
                        }
                        is State.Failed -> {
                            opponentLabel.setText(it.message)
                        }
                        is State.Success -> {
                            game.setScreen<GameplayScreen>()
                        }
                    }
                }
            }
        }
        backBtn.onClick {
            KtxAsync.launch {
                if (LobbyData.instance.game == null) {
                    game.setScreen<MenuScreen>()
                } else {
                    LobbyData.instance.cancelLobby()?.collect {
                        when (it) {
                            is State.Success -> {
                                game.setScreen<MenuScreen>()
                            }
                            is State.Loading -> {

                            }
                            is State.Failed -> {

                            }
                        }
                    }
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

    @ExperimentalCoroutinesApi
    override fun update(p0: Observable?, p1: Any?) {
        if (p1 is Game) {
            codeLabel.setText(p1.code)

            // Yeah, this is ugly! Listen for live updates on lobby with id
            KtxAsync.launch {
                LobbyRepository().subscribeToLobbyUpdates(p1.id).collect {
                    when (it) {
                        is State.Success -> {
                            // On received update: Check if opponent of updated Game is not null
                            if (it.data.opponent != null) {
                                // Player may now start the game
                                opponentLabel.setText("${it.data.opponent?.displayName} - ${it.data.opponent?.beardLength}cm");
                                startGameBtn.isVisible = true
                            } else {
                                opponentLabel.setText("Waiting for opponent to join");
                                startGameBtn.isVisible = false
                            }
                        }
                        is State.Loading -> {
                        }
                        is State.Failed -> {
                        }
                    }
                }
            }
        }
    }
}
