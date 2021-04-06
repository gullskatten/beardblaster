package no.ntnu.beardblaster.ui

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.scene2d.Scene2DSkin
import ktx.style.*
import no.ntnu.beardblaster.assets.AtlasAsset
import no.ntnu.beardblaster.assets.FontAsset
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

enum class FontStyle {
    Default
}

operator fun Skin.get(font: FontStyle): BitmapFont = this.getFont(font.name)

enum class LabelStyle {
    Heading,
}

enum class ButtonStyle {
    OK,
    Cancel,
}

fun createSkin(assets: AssetManager): Skin {
    Scene2DSkin.defaultSkin = skin(assets[AtlasAsset.Game]) { skin ->
        add(FontStyle.Default.name, assets[FontAsset.Standard])

        label {
            font = skin[FontStyle.Default]
            fontColor = Color.BLACK
        }

        label(LabelStyle.Heading.name) {
            font = skin[FontStyle.Default]
            fontColor = Color.BROWN
            background = skin[Image.ModalHeader]
        }

        button(ButtonStyle.OK.name) {
            up = skin[Image.DialogButtonOK]
            down = skin[Image.DialogButtonOK]
        }

        button(ButtonStyle.Cancel.name) {
            up = skin[Image.DialogButtonCancel]
            down = skin[Image.DialogButtonCancel]
        }

        textButton {
            font = skin[FontStyle.Default]
            up = skin[Image.Button]
            over = skin[Image.ButtonHover]
            down = skin[Image.ButtonPressed]
            pressedOffsetX = 4f
            pressedOffsetY = 4f
        }

        textField {
            font = skin[FontStyle.Default]
            fontColor = Color.BROWN
            messageFontColor = Color.GRAY
            background = skin[Image.InputDark]
            focusedBackground = skin[Image.InputLight]
        }
    }
    return Scene2DSkin.defaultSkin
}
