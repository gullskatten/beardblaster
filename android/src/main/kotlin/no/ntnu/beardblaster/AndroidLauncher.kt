package no.ntnu.beardblaster

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import no.ntnu.beardblaster.dbclasses.SpellDao
import no.ntnu.beardblaster.dbclasses.SpellDatabase

/** Launches the Android application.  */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SpellDatabase.initialize(this)
        val configuration = AndroidApplicationConfiguration()
        configuration.useImmersiveMode = true
        initialize(BeardBlasterGame(), configuration)
    }
}
