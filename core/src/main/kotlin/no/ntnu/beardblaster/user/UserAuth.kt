package no.ntnu.beardblaster.user

import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import no.ntnu.beardblaster.commons.User
import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser
import pl.mk5.gdx.fireapp.promises.Promise

interface IUserAuth {
    fun createUser(email: String, password: String, displayName: String)
    fun signIn(email: String, password: String): Promise<GdxFirebaseUser>
    fun signOut()
    fun isLoggedIn(): Boolean
}

private val LOG = logger<UserAuth>()

class UserAuth : IUserAuth {

    override fun createUser(email: String, password: String, displayName: String) {
        GdxFIRAuth.inst()
                .createUserWithEmailAndPassword(email, password.toCharArray())
                .then<GdxFirebaseUser> {
                    LOG.debug { "Created user: $displayName, ${it.userInfo.email}" }

                    val user = User(displayName, id=it.userInfo.uid)
                    UserRepository().create(user, "users")
                }
                .fail { s, _ ->
                    if (s.contains("The email address is already in use by another account")) {
                        LOG.debug { "Tried to create a duplicate user $email" }
                    }
                    LOG.error { "Create user failed $s" }
                    // TODO: SHOW ERROR TO USER (ON USER CREATION FAIL)
                }
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
}
