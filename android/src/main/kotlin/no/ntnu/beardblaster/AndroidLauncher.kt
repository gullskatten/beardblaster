package no.ntnu.beardblaster

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import no.ntnu.beardblaster.dbclasses.SpellDatabase

/** Launches the Android application.  */
class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Sletter databasen som allerede er lagret lokalt før man bygger ny.
        //Definitivt litt bad practice hvis man ønsker å lagre ting fra sessions persistent, men
        //gjør det lettere å oppdatere prepop-fila i assets. Bør fjernes når man sier seg fornøyd med den.
        context.deleteDatabase("beardblaster-db")
        SpellDatabase.initialize(this)
        val configuration = AndroidApplicationConfiguration()
        configuration.useImmersiveMode = true
        initialize(BeardBlasterGame(), configuration)
    }
}
