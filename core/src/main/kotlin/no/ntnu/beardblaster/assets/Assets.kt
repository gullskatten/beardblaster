package no.ntnu.beardblaster.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.I18NBundle
import ktx.assets.getAsset
import ktx.assets.load

enum class Font(val path: String) {
    Standard("font_nevis/nevis.fnt"),
}

fun AssetManager.load(asset: Font) = load<BitmapFont>(asset.path)
operator fun AssetManager.get(asset: Font) = getAsset<BitmapFont>(asset.path)

enum class Atlas(val path: String) {
    Game("bb_gui.atlas"),
}

fun AssetManager.load(asset: Atlas) = load<TextureAtlas>(asset.path)
operator fun AssetManager.get(asset: Atlas) = getAsset<TextureAtlas>(asset.path)

enum class I18N(val path: String) {
    Default("i18n/nls"),
}

fun AssetManager.load(asset: I18N) = load<I18NBundle>(asset.path)
operator fun AssetManager.get(asset: I18N) = getAsset<I18NBundle>(asset.path)
