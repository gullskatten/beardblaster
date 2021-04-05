package no.ntnu.beardblaster.ui

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import no.ntnu.beardblaster.assets.Atlas
import no.ntnu.beardblaster.assets.Font
import no.ntnu.beardblaster.assets.get

enum class Image(val atlasKey: String) {
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

operator fun Skin.get(image: Image): Drawable = this.getDrawable(image.atlasKey)

enum class FontType {
    Default
}

operator fun Skin.get(font: Font): BitmapFont = this.getFont(font.name)

fun createSkin(assets: AssetManager): Skin {
    Scene2DSkin.defaultSkin = skin(assets[Atlas.Game]) { skin ->
        add(FontType.Default.name, assets[Font.Standard])

        label {
            font = skin[FontType.Default]
            fontColor = Color.BLACK
        }

        label("heading") {
            font = skin[FontType.Default]
            fontColor = Color.BROWN
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
            font = skin[FontType.Default]
            up = skin[Image.Button]
            over = skin[Image.ButtonHover]
            down = skin[Image.ButtonPressed]
            pressedOffsetX = 4f
            pressedOffsetY = 4f
        }

        textField {
            font = skin[FontType.Default]
            fontColor = Color.BROWN
            messageFontColor = Color.GRAY
            background = skin[Image.InputDark]
            focusedBackground = skin[Image.InputLight]
        }
    }
    return Scene2DSkin.defaultSkin
}
