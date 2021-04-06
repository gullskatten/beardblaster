package no.ntnu.beardblaster.assets

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.utils.I18NBundle
import ktx.assets.async.AssetStorage

enum class FontAsset(val path: String) {
    Default("font_nevis/nevis.fnt"),
}

fun AssetStorage.loadAsync(asset: FontAsset) = loadAsync<BitmapFont>(asset.path)
operator fun AssetStorage.get(asset: FontAsset) = get<BitmapFont>(asset.path)

enum class AtlasAsset(val path: String) {
    UI("bb_gui.atlas"),
}

fun AssetStorage.loadAsync(asset: AtlasAsset) = loadAsync<TextureAtlas>(asset.path)
operator fun AssetStorage.get(asset: AtlasAsset) = get<TextureAtlas>(asset.path)

enum class I18NAsset(val path: String) {
    Default("i18n/nls"),
}

fun AssetStorage.loadAsync(asset: I18NAsset) = loadAsync<I18NBundle>(asset.path)
operator fun AssetStorage.get(asset: I18NAsset) = get<I18NBundle>(asset.path)
