package no.ntnu.beardblaster.ui

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.button
import ktx.scene2d.scene2d
import ktx.scene2d.table

class InputDialog(
    private val title: String,
    style: String = Style.Dialog.name
) : Dialog("", Scene2DSkin.defaultSkin, style) {
    val okBtn = scene2d.button(ButtonStyle.OK.name).apply { isDisabled = true }
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
    }

    private val inputMap = HashMap<String, TextField>()
    val data: Map<String, String>
        get() = inputMap.map { (key, value) -> key to value.text }.toMap()

    fun input(key: String, messageText: String, passwordMode: Boolean = false) {
        val inputField = inputField(messageText, passwordMode).apply {
            onChange { updateButtonSensitivity() }
        }
        inputMap[key] = inputField
        inputTable.apply {
            row()
            add(inputField).growX()
        }
    }

    private fun updateButtonSensitivity() {
        okBtn.isDisabled = inputMap.filter { it.value.text == "" }.isNotEmpty()
    }
}
