package no.ntnu.beardblaster

import com.badlogic.gdx.Application
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import pl.mk5.gdx.fireapp.GdxFIRApp

class BeardBlasterGame : ApplicationAdapter() {
    private val EMAIL = "beard@blaster.com"
    private val PASSWORD = "beardblaster"

    override fun create() {
        super.create()
        Gdx.app.logLevel = Application.LOG_DEBUG;
        GdxFIRApp.inst().configure();
        val inst = UserAuth()
        inst.createUser(EMAIL, PASSWORD)
        inst.signOut()
        inst.signIn(EMAIL, PASSWORD)
    }
}