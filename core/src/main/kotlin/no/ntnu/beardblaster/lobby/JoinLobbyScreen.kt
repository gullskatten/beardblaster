package no.ntnu.beardblaster.lobby

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BaseScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.WORLD_WIDTH
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.*

class JoinLobbyScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), JoinLobbyPresenter.View {
    private val presenter = JoinLobbyPresenter(this, game)
    private lateinit var waitingLabel: Label
    private lateinit var errorLabel: Label
    private lateinit var codeInput: TextField
    private lateinit var submitCodeBtn: TextButton
    private lateinit var backBtn: Button

    override fun initScreen() {
        presenter.init()
        codeInput = inputField(Nls.gameCode())
        submitCodeBtn = scene2d.textButton(Nls.submit())
        backBtn = scene2d.button(ButtonStyle.Cancel.name)

        waitingLabel = bodyLabel("", 1.5f, LabelStyle.BodyOutlined.name)
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
            this.add(waitingLabel)
            this.add(errorLabel)
            this.row()
            this.add(submitCodeBtn)
        }
        val table = fullSizeTable().apply {
            background = skin[Image.BackgroundSecondary]
            add(backBtn).expandY().top().padTop(50f).width(91f)
            add(content).width(WORLD_WIDTH * 0.9f).fillY()
        }
        stage.addActor(table)
    }

    override fun setBtnEventListeners() {
        submitCodeBtn.onClick {
            presenter.onSubmitCodeBtnClick(codeInput.text)
        }
        backBtn.onClick {
            presenter.onBackBtnClick()
        }
    }

    override fun setErrorLabel(msg: String) {
        errorLabel.setText(msg)
    }

    override fun setWaitingLabel(msg: String) {
        waitingLabel.setText(msg)
    }

    override fun setWaitingLabelFontScale(scale: Float) {
        waitingLabel.setFontScale(scale)
    }

    override fun setErrorLabelVisibility(isVisible: Boolean) {
        errorLabel.isVisible = isVisible
    }

    override fun isErrorLabelVisible(): Boolean {
        return errorLabel.isVisible
    }

    override fun setWaitingLabelVisibility(isVisible: Boolean) {
        waitingLabel.isVisible = isVisible
    }

    override fun setSubmitCodeBtnVisibility(isVisible: Boolean) {
        submitCodeBtn.isVisible = isVisible
    }
}
