package no.ntnu.beardblaster.user

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table
import no.ntnu.beardblaster.assets.Nls
import no.ntnu.beardblaster.ui.*

open class InputDialog(
    private val title: String,
    style: String = Style.Dialog.name
) : Dialog("", Scene2DSkin.defaultSkin, style) {
    val okBtn = scene2d.button(ButtonStyle.OK.name)
    val cancelBtn = scene2d.button(ButtonStyle.Cancel.name)
    private val inputTable = scene2d.table {
        defaults().pad(25f, 100f, 25f, 100f).align(Align.top)
        background = skin[Image.Modal]
    }

    init {
        isModal = true
        isTransform = false
        defaults().space(0f)
        contentTable.apply {
            defaults().space(0f)
            add(cancelBtn).top().padTop(200f)
            add(scene2d.table {
                add(headingLabel(title)).expandX()
                row()
                add(inputTable)
            }).expand()
            add(okBtn).top().padTop(200f)
            pack()
        }
        okBtn.isDisabled = true
        cancelBtn.onChange { hide() }
    }

    private val inputMap = HashMap<String, TextField>()

    val isValid: Boolean
        get() = inputMap.filter { it.value.text.isBlank() }.isEmpty()

    val data: Map<String, String>
        get() = inputMap.map { (key, value) -> key to value.text }.toMap()

    fun input(key: String, messageText: String, passwordMode: Boolean = false) {
        input(key, inputField(messageText, passwordMode))
    }

    fun input(key: String, inputField: TextField) {
        inputField.onChange { updateBtnSensitivity() }
        inputMap[key] = inputField
        inputTable.apply {
            row()
            add(inputField).growX()
        }
    }

    private fun updateBtnSensitivity() {
        okBtn.isDisabled = !isValid
    }
}

class LoginDialog : InputDialog(Nls.login()) {
    private val emailInput = inputField(Nls.emailAddress())
    private val passwordInput = passwordField(Nls.password())

    init {
        input("email", emailInput)
        input("password", passwordInput)
    }

    val email: String get() = emailInput.text
    val password: String get() = passwordInput.text
}

class RegisterDialog : InputDialog(Nls.register()) {
    private val usernameInput = inputField(Nls.wizardName())
    private val emailInput = inputField(Nls.emailAddress())
    private val passwordInput = passwordField(Nls.password())

    init {
        input("username", usernameInput)
        input("email", emailInput)
        input("password", passwordInput)
    }

    val username: String get() = usernameInput.text
    val email: String get() = emailInput.text
    val password: String get() = passwordInput.text
}
