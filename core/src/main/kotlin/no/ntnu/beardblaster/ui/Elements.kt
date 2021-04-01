package no.ntnu.beardblaster.ui

import com.badlogic.gdx.scenes.scene2d.ui.TextField
import ktx.scene2d.scene2d
import ktx.scene2d.textField

fun inputField(messageText: String, password: Boolean = false): TextField {
    return scene2d.textField {
        this.text = ""
        this.messageText = messageText
        if (password) {
            isPasswordMode = true
            setPasswordCharacter("*"[0])
        }
    }
}

fun passwordField(messageText: String): TextField {
    return inputField(messageText, true)
}
