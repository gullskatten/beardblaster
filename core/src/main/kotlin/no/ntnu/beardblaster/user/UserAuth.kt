package no.ntnu.beardblaster.user

import ktx.log.debug
import ktx.log.error
import ktx.log.logger
import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser

interface IUserAuth {
    fun createUser(email: String, password: String, displayName: String)
    fun signIn(email: String, password: String)
    fun signOut()
    fun isLoggedIn(): Boolean
}

private val LOG = logger<UserAuth>()

class UserAuth : IUserAuth {

    override fun createUser(email: String, password: String, displayName: String) {
        GdxFIRAuth.inst()
                .createUserWithEmailAndPassword(email, password.toCharArray())
                .then<GdxFirebaseUser> {
                    LOG.debug { "Created user: $displayName, ${GdxFIRAuth.inst().currentUser?.userInfo?.email}" }
                    // TODO: Add user to /users/${GdxFIRAuth.inst().currentUser?.userInfo?.uid} with displayName and beardLength = 0
                }
                .fail { s, _ ->
                    if (s.contains("The email address is already in use by another account")) {
                        LOG.debug { "Tried to create a duplicate user $email" }
                    }
                    LOG.error { "Create user failed $s" }
                }
    }

    override fun signIn(email: String, password: String) {
        GdxFIRAuth.inst()
                .signInWithEmailAndPassword(email, password.toCharArray())
                .then<GdxFirebaseUser> { gdxFirebaseUser ->
                    LOG.debug { "Signed in. currentUser: ${GdxFIRAuth.inst().currentUser?.userInfo?.email}" }
                }
                .fail { s, _ ->
                    if (s.contains("")) {
                        LOG.debug { "No user exists with those credentials: ($email, $password) - $s" }
                    }
                }
    }

    override fun signOut() {
        GdxFIRAuth.inst().signOut()
                .then<Void> {
                    LOG.debug { "Signed out. currentUser: ${GdxFIRAuth.inst().currentUser}" }
                }
                .fail { s, _ ->
                    if (s.contains("")) {
                        LOG.debug { "Already logged out $s" }
                    }
                }
    }

    override fun isLoggedIn(): Boolean {
        LOG.debug { "Check current user: ${GdxFIRAuth.inst().currentUser?.userInfo?.email}" }
        return GdxFIRAuth.inst().currentUser !== null
    }
}