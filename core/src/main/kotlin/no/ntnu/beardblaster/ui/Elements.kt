package no.ntnu.beardblaster.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import ktx.scene2d.label
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

fun headingLabel(text: String): Label {
    return scene2d.label(text, "heading") {
        setAlignment(Align.center)
        setFontScale(2f)
    }
}
