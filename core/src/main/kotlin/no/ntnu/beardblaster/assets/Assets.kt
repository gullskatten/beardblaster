package no.ntnu.beardblaster.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.I18NBundle
import ktx.assets.getAsset
import ktx.assets.load

enum class FontAsset(val path: String) {
    Default("font_nevis/nevis.fnt"),
}

fun AssetManager.load(asset: FontAsset) = load<BitmapFont>(asset.path)
operator fun AssetManager.get(asset: FontAsset) = getAsset<BitmapFont>(asset.path)

enum class AtlasAsset(val path: String) {
    UI("bb_gui.atlas"),
}

fun AssetManager.load(asset: AtlasAsset) = load<TextureAtlas>(asset.path)
operator fun AssetManager.get(asset: AtlasAsset) = getAsset<TextureAtlas>(asset.path)

enum class I18NAsset(val path: String) {
    Default("i18n/nls"),
}

fun AssetManager.load(asset: I18NAsset) = load<I18NBundle>(asset.path)
operator fun AssetManager.get(asset: I18NAsset) = getAsset<I18NBundle>(asset.path)
