package no.ntnu.beardblaster.ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ktx.scene2d.textField
import no.ntnu.beardblaster.WORLD_HEIGHT
import no.ntnu.beardblaster.WORLD_WIDTH

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
    return scene2d.label(text, LabelStyle.Heading.name) {
        setAlignment(Align.center)
        setFontScale(2f)
    }
}

fun bodyLabel(text: String, fontScale: Float = 1.5f, labelStyle: String = LabelStyle.Body.name): Label {
    return scene2d.label(text, labelStyle) {
        setAlignment(Align.center)
        setFontScale(fontScale)
    }
}

fun fullSizeTable(pad: Float = 0f): Table {
    return scene2d.table {
        setBounds(0f, 0f, WORLD_WIDTH, WORLD_HEIGHT)
        defaults().pad(pad)
    }
}

fun smallTable(pad: Float = 0f): Table {
    return scene2d.table {
        setBounds(WORLD_WIDTH-250f, WORLD_HEIGHT-150f, 250f, 150f )
        defaults().pad(pad)
    }
}
