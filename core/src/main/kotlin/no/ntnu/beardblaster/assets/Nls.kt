package no.ntnu.beardblaster.assets

/** Generated from assets/i18n/nls.properties. Do not edit! */

import com.badlogic.gdx.utils.I18NBundle
import ktx.i18n.BundleLine

@Suppress("EnumEntryName")
enum class Nls : BundleLine {
    appName,
    back,
    close,
    createGame,
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
    forfeit,
    quitGame,
    register,
    selectMaxElement,
    selectMoreElements,
    selectOneMoreElement,
    shareGameCodeMessage,
    startGame,
    submit,
    tutorial,
    tutorialGuide,
    wizardName,
    youLose,
    youReceive,
    youWin,
    waitingForHostToStart,
    failedToLeaveLobby,
    failedToJoinLobby,
    verifyingCode,
    startingGame,
    worthyOpponentJoined,
    waitingForOpponentToJoin,
    calculatingSpells,
    holdOn,
    unknown,
    creatingGame,
    bothWizardWereIdle,
    noWizardsWantedToCastSpell,
    loadingUser,
    failedToLoadUserData,
    loading,
    lockSpell,
    pleaseWait,
    waitingForOpponent,
    tryAgain;

    override val bundle: I18NBundle
        get() = i18nBundle

    companion object {
        lateinit var i18nBundle: I18NBundle
    }
}

