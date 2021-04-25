package no.ntnu.beardblaster.leaderboard

import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.BeardBlasterGame
import no.ntnu.beardblaster.commons.leaderboard.BeardScore
import no.ntnu.beardblaster.menu.MenuScreen
import java.util.*

private val LOG = logger<LeaderBoardPresenter>()

class LeaderBoardPresenter(private val view: View, val game: BeardBlasterGame) : Observer {
    private val leaderBoardHandler = LeaderBoardHandler()

    interface View {
        fun fillLeaderBoard(beardScores: List<BeardScore>)
    }

    fun init() {
        leaderBoardHandler.addObserver(this)
        KtxAsync.launch {
            LOG.debug { "Summoning the beards…" }
            leaderBoardHandler.getTopTenBeards()
        }
    }

    fun onBackBtnClick() {
        game.setScreen<MenuScreen>()
        dispose()
    }


    override fun update(o: Observable, arg: Any?) {
        if (o is LeaderBoardHandler) {
            view.fillLeaderBoard(o.topTen)
        }
    }

    fun dispose() {
        leaderBoardHandler.deleteObserver(this)
    }
}

