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
import ktx.actors.onClick
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Assets
import no.ntnu.beardblaster.utils.AbstractScreen
import no.ntnu.beardblaster.utils.BeardBlasterStage


private val LOG = logger<GameplayScreen>()


class GameplayScreen(game: BeardBlasterGame) : AbstractScreen(game) {
    private lateinit var skin: Skin
    private lateinit var table: Table
    private lateinit var heading: Label

    private lateinit var btnAttack: TextButton
    private lateinit var btnQuit: TextButton

    private val gameplayStage: Stage by lazy {
        val result = BeardBlasterStage()
        Gdx.input.inputProcessor = result
        result
    }

    override fun show() {
        LOG.debug { "GAMEPLAY Screen" }

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

        btnAttack = TextButton("ATTACK", textButtonStyle)
        btnQuit = TextButton("QUIT", textButtonStyle)
        setBtnEventListeners()

        Label.LabelStyle(standardFont, Color.BLACK).also {
            heading = Label("Preparation phase", it)
            heading.setFontScale(2f)
            it.background = skin.getDrawable("modal_fancy_header")
            heading.setAlignment(Align.center)
        }

        // Creating table
        table.add(heading).pad(50f)
        table.row()
        table.add(btnAttack).pad(40f)
        table.row()
        table.add(btnQuit).pad(40f)
        table.row()

        // Adding actors to the stage
        gameplayStage.addActor(table)

        Gdx.input.inputProcessor = gameplayStage

    }

    override fun update(delta: Float) {
    }

    override fun setBtnEventListeners() {
        btnAttack.onClick {
            LOG.debug { "Wizard 1 attacks" }
        }
        btnQuit.onClick {
            game.removeScreen<GameplayScreen>()
            game.addScreen(GameplayScreen(game))
            game.setScreen<MenuScreen>()
        }
    }

    override fun render(delta: Float) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        update(delta)

        gameplayStage.act(delta)
        gameplayStage.draw()
    }


}
