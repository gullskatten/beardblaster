package no.ntnu.beardblaster.assets

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
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
