package no.ntnu.beardblaster.assets

/** Generated from assets/i18n/nls.properties. Do not edit! */

import com.badlogic.gdx.utils.I18NBundle
import ktx.i18n.BundleLine

@Suppress("EnumEntryName")
enum class Nls : BundleLine {
    appName,
    attack,
    back,
    close,
    confirmPassword,
    createGame,
    createWizard,
    emailAddress,
    exitGame,
    gameCode,
    itsADraw,
    joinGame,
    leaderBeard,
    lobby,
    logIn,
    login,
    logOut,
    opponentReceived,
    password,
    preparationPhase,
    waitingPhase,
    actionPhase,
    gameOverPhase,
    quit,
    quitGame,
    register,
    selectMaxElement,
    selectMoreElements,
    selectOneMoreElement,
    shareGameCodeMessage,
    startGame,
    submit,
    tutorial,
    welcomeWizard,
    wizardName,
    wizardNAttacks,
    youLose,
    youReceive,
    youWin;

    override val bundle: I18NBundle
        get() = i18nBundle

    companion object {
        lateinit var i18nBundle: I18NBundle
    }
}

