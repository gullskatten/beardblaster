package no.ntnu.beardblaster.user

import kotlinx.coroutines.flow.collect
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.user.User
import no.ntnu.beardblaster.leaderboard.LeaderBoardRepository
import pl.mk5.gdx.fireapp.GdxFIRAuth
import java.util.*

private val LOG = logger<UserData>()

class UserData private constructor() : Observable() {
    var user: User? = null
        private set

    var error: String? = null
        private set

    var isLoading: Boolean = false
        private set

    fun setUserData(user: User?) {
        this.user = user
    }


    suspend fun loadUserData() {
        if (user == null) {
            UserRepository().getDocument(GdxFIRAuth.inst().currentUser.userInfo.uid, "users")
                .collect {

                    when (it) {
                        is State.Success -> {
                            isLoading = false
                            LOG.info { "Found Current User (!): ${it.data.displayName}" }
                            LOG.info { "Loading beard for current user..." }
                            LeaderBoardRepository().getBeardLengthForUser(GdxFIRAuth.inst().currentUser.userInfo.uid)
                                .collect { state ->
                                    when(state) {
                                        is State.Success -> {
                                            LOG.info { "Beard of ${state.data}cm locked and loaded..." }
                                            it.data.beardLength = state.data
                                            setUserData(it.data)
                                            notifyObservers(it.data)
                                        }
                                        is State.Failed -> {
                                        }
                                        is State.Loading -> {

                                        }
                                    }
                                }
                            error = null
                        }
                        is State.Loading -> {
                            isLoading = true
                            LOG.info { "Loading user data!" }
                            notifyObservers("Loading user..")
                            error = null
                        }
                        is State.Failed -> {
                            isLoading = false
                            LOG.info { "Loading user FAILED: ${it.message}" }
                            notifyObservers("Failed to load user data!")
                            error = it.message
                        }
                    }
                    setChanged()
                }
        }

    }

    fun getCurrentUserString(): String {
        user?.let { return user!!.displayName }
        return "Loading User.."
    }

    fun getBeardLength(): Float {
        user?.let { return user!!.beardLength }
        return 0f
    }

    companion object {
        val instance = UserData()
    }
}
