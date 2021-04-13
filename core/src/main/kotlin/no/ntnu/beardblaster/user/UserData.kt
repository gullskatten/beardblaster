package no.ntnu.beardblaster.user

import kotlinx.coroutines.flow.collect
import ktx.log.info
import ktx.log.logger
import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.User
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
                            setUserData(it.data)
                            LOG.info { "Found Current User (!): ${it.data.displayName}" }
                            notifyObservers(getCurrentUserString())
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
        user?.let { return "${user!!.displayName} - ${user!!.beardLength}cm" }
        return ""
    }

    companion object {
        val instance = UserData()
    }
}
