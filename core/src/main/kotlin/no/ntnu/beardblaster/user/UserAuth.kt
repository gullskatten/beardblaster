package no.ntnu.beardblaster

import com.badlogic.gdx.Gdx
import pl.mk5.gdx.fireapp.GdxFIRAuth
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser

interface IUserAuth {
    fun createUser(email: String, password: String, displayName: String)
    fun signIn(email: String, password: String)
    fun signOut()
}

class UserAuth : IUserAuth {

    override fun createUser(email: String, password: String, displayName: String) {
        GdxFIRAuth.inst()
                .createUserWithEmailAndPassword(email, password.toCharArray())
                .then<GdxFirebaseUser> {
                    Gdx.app.debug("Create user", "Created user: " + GdxFIRAuth.inst().currentUser?.userInfo?.email)
                    // TODO: Add user to /users/${GdxFIRAuth.inst().currentUser?.userInfo?.uid} with displayName and beardLength = 0
                }
                .fail { s, _ ->
                    if (s.contains("The email address is already in use by another account")) {
                        Gdx.app.debug("Create user", "Tried to create a duplicate user")
                    }
                    Gdx.app.error("Create user failed", s)
                }
    }

    override fun signIn(email: String, password: String) {
        GdxFIRAuth.inst()
                .signInWithEmailAndPassword(email, password.toCharArray())
                .then<GdxFirebaseUser> { gdxFirebaseUser ->
                    Gdx.app.debug("Sign in user", "Signed in. currentUser: " + GdxFIRAuth.inst().currentUser?.userInfo?.email)
                }
                .fail { s, _ ->
                    if (s.contains("")) {
                        Gdx.app.debug("Sign in user", "No user exists with those credentials" + s)
                    }
                }
    }

    override fun signOut() {
        GdxFIRAuth.inst().signOut()
                .then<Void> {
                    Gdx.app.debug("Sign out user", "Signed out. currentUser: " + GdxFIRAuth.inst().currentUser)
                }
                .fail { s, _ ->
                    if (s.contains("")) {
                        Gdx.app.debug("Sign out user", "Already logged out" + s)
                    }
                }
    }
}