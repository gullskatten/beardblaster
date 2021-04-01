package no.ntnu.beardblaster.ui

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import no.ntnu.beardblaster.assets.Atlas
import no.ntnu.beardblaster.assets.Font
import no.ntnu.beardblaster.assets.get

enum class Image(val region: String) {
    // Buttons
    Button("button_default"),
    ButtonHover("button_default_hover"),
    ButtonPressed("button_default_pressed"),

    // Dialog buttons
    DialogButtonOK("modal_fancy_header_button_green_check"),
    DialogButtonCancel("modal_fancy_header_button_red_cross_left"),

    // Input elements
    InputDark("input_texture_dark"),
    InputLight("input_texture_light"),

    // Backgrounds
    Background("background"),
    Modal("modal_fancy"),
    ModalSkull("modal_fancy_skull"),
    ModalHeader("modal_fancy_header"),
}

enum class FontType(val key: String) {
    Default("default"),
}

operator fun Skin.get(image: Image): Drawable = this.getDrawable(image.region)

fun createSkin(assets: AssetManager): Skin {
    Scene2DSkin.defaultSkin = skin(assets[Atlas.Game]) { skin ->
        add(FontType.Default.key, assets[Font.Standard])

        label {
            font = skin.getFont(FontType.Default.key)
            fontColor = Color.BLACK
        }

        label("heading") {
            font = skin.getFont(FontType.Default.key)
            fontColor = Color.BLACK
            background = skin[Image.ModalHeader]
        }

        button("ok") {
            up = skin[Image.DialogButtonOK]
            down = skin[Image.DialogButtonOK]
        }

        button("cancel") {
            up = skin[Image.DialogButtonCancel]
            down = skin[Image.DialogButtonCancel]
        }

        textButton {
            font = skin.getFont(FontType.Default.key)
            up = skin[Image.Button]
            over = skin[Image.ButtonHover]
            down = skin[Image.ButtonPressed]
            pressedOffsetX = 4f
            pressedOffsetY = 4f
        }

        textField {
            font = skin.getFont(FontType.Default.key)
            fontColor = Color.BROWN
            messageFontColor = Color.GRAY
            background = skin[Image.InputDark]
            focusedBackground = skin[Image.InputLight]
        }
    }
    return Scene2DSkin.defaultSkin
}
