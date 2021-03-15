package no.ntnu.beardblaster.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas


object Assets {

    //Define all assets that must be loaded during loading screen
    var assetManager = AssetManager()
    val loadBar = AssetDescriptor<Texture>("graphics/load_bar.png", Texture::class.java)
    val loginImg = AssetDescriptor<Texture>("graphics/login.png", Texture::class.java)
    val test1 = AssetDescriptor<Texture>("bar_container.png", Texture::class.java)
    val test2 = AssetDescriptor<Texture>("bb_gui.png", Texture::class.java)
    val test3 = AssetDescriptor<Texture>("bg_modal_fancy.png", Texture::class.java)
    val test4 = AssetDescriptor<Texture>("bg_modal_small.png", Texture::class.java)
    val test5 = AssetDescriptor<Texture>("bg_modal_wooden.png", Texture::class.java)
    val test6 = AssetDescriptor<Texture>("button_fancy_dark_icon.png", Texture::class.java)
    val atlas = AssetDescriptor<TextureAtlas>("bb_gui.atlas", TextureAtlas::class.java)

    //fonts
    val standardFont =  AssetDescriptor<BitmapFont>("font_nevis/nevis.fnt", BitmapFont::class.java)


    //Load all assets
    fun loadTextures() {
        assetManager.load(loadBar)
        assetManager.load(loginImg)
        assetManager.load(test1)
        assetManager.load(test2)
        assetManager.load(test3)
        assetManager.load(test4)
        assetManager.load(test5)
        assetManager.load(test6)
        assetManager.load(atlas)
        assetManager.load(standardFont)

    }
    fun dispose() {
        assetManager.dispose()
    }

}