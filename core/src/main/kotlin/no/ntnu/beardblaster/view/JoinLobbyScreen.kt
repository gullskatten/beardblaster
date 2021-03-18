package no.ntnu.beardblaster.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.AbstractScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets
import no.ntnu.beardblaster.worldHeight
import no.ntnu.beardblaster.worldWidth

private val LOG = logger<JoinLobbyScreen>()

class JoinLobbyScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    private lateinit var skin: Skin
    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var btnStartGame: TextButton
    private lateinit var btnBack: TextButton

    private val joinLobbyStage: Stage by lazy {
        val result = Stage(FitViewport(worldWidth, worldHeight))
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        LOG.debug { "JOIN LOBBY Screen" }

        skin = Skin(Assets.assetManager.get(Assets.atlas))
        table = Table(skin)
        table.setBounds(0f, 0f, viewport.worldWidth, viewport.worldHeight)

        // Fonts
        val standardFont = Assets.assetManager.get(Assets.standardFont)

        // Creating buttons
        val textButtonStyle = TextButton.TextButtonStyle()
        skin.getDrawable("button_default").also { textButtonStyle.up = it }
        skin.getDrawable("button_default_hover").also { textButtonStyle.over = it }
        skin.getDrawable("button_default_pressed").also { textButtonStyle.down = it }
        textButtonStyle.pressedOffsetX = 4f
        textButtonStyle.pressedOffsetY = -4f
        textButtonStyle.font = standardFont

        btnStartGame = TextButton("START GAME", textButtonStyle)
        btnBack = TextButton("GO BACK", textButtonStyle)

        // Creating heading
        Label.LabelStyle(standardFont, Color.BLACK).also {
            heading = Label("JOIN LOBBY", it)
            heading.setFontScale(2f)
            it.background = skin.getDrawable("modal_fancy_header")
            heading.setAlignment(Align.center)
        }

        // Creating table
        table.background = skin.getDrawable("modal_fancy")
        table.add(heading).pad(50f)
        table.row()
        table.add(btnStartGame).pad(40f)
        table.row()
        table.add(btnBack).pad(40f)
        table.row()

        // Adding actors to the stage
        joinLobbyStage.addActor(table)
    }

    override fun update(delta: Float) {
        btnStartGame.onClick {
            game.setScreen<GameplayScreen>()
        }
        btnBack.onClick {
            game.setScreen<MenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        joinLobbyStage.act(delta)
        joinLobbyStage.draw()
    }


}
