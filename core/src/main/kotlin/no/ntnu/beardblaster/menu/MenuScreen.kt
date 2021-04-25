package no.ntnu.beardblaster.menu

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import ktx.actors.onClick
import ktx.assets.async.AssetStorage
import ktx.log.logger
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textButton
import no.ntnu.beardblaster.BaseScreen
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.leaderboard.BeardScale
import no.ntnu.beardblaster.ui.*
import no.ntnu.beardblaster.user.UserData

private val LOG = logger<MenuScreen>()

class MenuScreen(
    game: BeardBlasterGame,
    batch: SpriteBatch,
    assets: AssetStorage,
    camera: OrthographicCamera,
) : BaseScreen(game, batch, assets, camera), MenuPresenter.View {
    private val presenter = MenuPresenter(this, game)

    private lateinit var createGameBtn: TextButton
    private lateinit var joinGameBtn: TextButton
    private lateinit var leaderBoardBtn: TextButton
    private lateinit var tutorialBtn: TextButton
    private lateinit var logoutBtn: TextButton
    private lateinit var exitBtn: TextButton
    private lateinit var wizardHeading: Label

    private val currentWizardLabel: Label =
        bodyLabel(
            UserData.instance.getCurrentUserString(),
            1.5f,
            LabelStyle.Body.name
        )
    private val currentWizardBeardLabel: Label = bodyLabel(
        "",
        1.5f,
        LabelStyle.Body.name
    )

    override fun initScreen() {
        createGameBtn = scene2d.textButton(Nls.createGame())
        joinGameBtn = scene2d.textButton(Nls.joinGame())
        leaderBoardBtn = scene2d.textButton(Nls.leaderBeard())
        tutorialBtn = scene2d.textButton(Nls.tutorial())
        logoutBtn = scene2d.textButton(Nls.logOut())
        exitBtn = scene2d.textButton(Nls.exitGame())
        wizardHeading = headingLabel("BeardBlaster")

        val wizNameTable = scene2d.table {
            defaults().space(25f)
            add(currentWizardLabel).left()
            add(currentWizardBeardLabel).right()
            background = dimmedLabelBackground()
        }

        val table = fullSizeTable(20f).apply {
            background = skin[Image.BackgroundSecondary]
            add(wizardHeading).colspan(4).center()
            row()
            add(wizNameTable).colspan(4).center()
            row()
            add(createGameBtn).colspan(4).center()
            row()
            add(joinGameBtn).colspan(4).center()
            row()
            add(leaderBoardBtn).colspan(2).center()
            add(tutorialBtn).colspan(2).center()
            row()
            add(logoutBtn).colspan(2).center()
            add(exitBtn).colspan(2).center()
        }
        stage.addActor(table)
        presenter.init()
    }

    override fun updateWizardLabel(string: String) {
        currentWizardLabel.setText(string)
    }

    override fun updateBeardLengthLabel(beardLength: Float) {
        currentWizardBeardLabel.setText("${beardLength}cm")
        currentWizardBeardLabel.color = BeardScale.getBeardColor(beardLength)
    }

    override fun setDisabledButtons(isDisabled: Boolean) {
        createGameBtn.touchable = if (isDisabled) Touchable.disabled else Touchable.enabled
        createGameBtn.isDisabled = isDisabled
        joinGameBtn.isDisabled = isDisabled
        joinGameBtn.touchable = if (isDisabled) Touchable.disabled else Touchable.enabled
        leaderBoardBtn.isDisabled = isDisabled
        leaderBoardBtn.touchable = if (isDisabled) Touchable.disabled else Touchable.enabled
        logoutBtn.isDisabled = isDisabled
        logoutBtn.touchable = if (isDisabled) Touchable.disabled else Touchable.enabled
    }

    override fun setBtnEventListeners() {
        createGameBtn.onClick {
            presenter.onCreateGameBtnClick()
        }
        joinGameBtn.onClick {
            presenter.onJoinGameBtnClick()
        }
        leaderBoardBtn.onClick {
            presenter.onLeaderBoardBtnClick()
        }
        tutorialBtn.onClick {
            presenter.onTutorialBtnClick()
        }
        logoutBtn.onClick {
            presenter.onLogoutBtnClick()
        }
        exitBtn.onClick {
            presenter.onExitBtnClick()
        }
    }

    override fun dispose() {
        super.dispose()
        presenter.dispose()
    }
}
