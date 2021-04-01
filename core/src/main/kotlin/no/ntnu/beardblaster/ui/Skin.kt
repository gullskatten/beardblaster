package no.ntnu.beardblaster.ui

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import ktx.scene2d.Scene2DSkin
import ktx.style.label
import ktx.style.skin
import ktx.style.textButton
import ktx.style.textField
import no.ntnu.beardblaster.assets.Atlas
import no.ntnu.beardblaster.assets.Font
import no.ntnu.beardblaster.assets.get

enum class Image(val region: String) {
    Button("button_default"),
    ButtonPressed("button_default_pressed"),

    Modal("modal_fancy"),
    ModalSkull("modal_fancy_skull"),
    ModalHeader("modal_fancy_header"),

    Background("background"),

    InputDark("input_texture_dark"),
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
        }

        label("heading") {
            font = skin.getFont(FontType.Default.key)
            fontColor = Color.BLACK
            background = skin[Image.ModalHeader]
        }

        textButton {
            down = skin[Image.ButtonPressed]
            up = skin[Image.Button]
            font = skin.getFont(FontType.Default.key)
        }

        textField {
            font = skin.getFont(FontType.Default.key)
            fontColor = Color.BROWN
            messageFontColor = Color.GRAY
            background = skin[Image.InputDark]
        }
    }
    return Scene2DSkin.defaultSkin
}
