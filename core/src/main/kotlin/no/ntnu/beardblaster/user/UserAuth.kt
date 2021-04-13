package no.ntnu.beardblaster.user

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import ktx.log.debug
import ktx.log.logger
import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser
import pl.mk5.gdx.fireapp.promises.Promise

interface IUserAuth {
    fun createUser(email: String, password: String, displayName: String) : Promise<GdxFirebaseUser>
    fun signIn(email: String, password: String): Promise<GdxFirebaseUser>
    fun signOut()
    fun isLoggedIn(): Boolean
    fun loginDevUser()
}

private val LOG = logger<UserAuth>()

class UserAuth : IUserAuth {

    override fun createUser(email: String, password: String, displayName: String): Promise<GdxFirebaseUser> {
        return GdxFIRAuth.inst()
            .createUserWithEmailAndPassword(email, password.toCharArray())
    }


    override fun signIn(email: String, password: String): Promise<GdxFirebaseUser> {
        return GdxFIRAuth.inst().signInWithEmailAndPassword(email, password.toCharArray())
    }

    override fun signOut() {
        GdxFIRAuth.inst().signOut()
            .then<Void> {
                LOG.debug { "Signed out. currentUser: ${GdxFIRAuth.inst().currentUser}" }
            }
            .fail { s, _ ->
                LOG.debug { "Already logged out $s" }
            }
    }

    override fun isLoggedIn(): Boolean {
        LOG.debug { "Check current user: ${GdxFIRAuth.inst().currentUser?.userInfo?.email}" }
        return GdxFIRAuth.inst().currentUser !== null
    }

    override fun loginDevUser() {
        if (!isLoggedIn() && Gdx.app.logLevel == Application.LOG_DEBUG) signIn(
            "beardy@beardy.no",
            "beardy"
        )
    }
}
