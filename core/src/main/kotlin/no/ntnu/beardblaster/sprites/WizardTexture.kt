package no.ntnu.beardblaster.sprites

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import ktx.assets.async.AssetStorage
import ktx.log.debug
import ktx.log.logger
import no.ntnu.beardblaster.assets.AtlasAsset
import no.ntnu.beardblaster.assets.get
import no.ntnu.beardblaster.screen.GameplayScreen

private val log = logger<GameplayScreen>()
class WizardTexture {
    private lateinit var animation: Animation
    private lateinit var position: Vector3
    private lateinit var bounds: Rectangle

    fun setAnimation(x: Float, y: Float, assets: AssetStorage, wizardTexture: WizardTextures) {
        var framecount = 0
        when (wizardTexture) {
            WizardTextures.GoodWizardAttack2 -> framecount = 8
            WizardTextures.GoodWizardAttack1 -> framecount = 8
            WizardTextures.GoodWizardIdle -> framecount = 6
            WizardTextures.EvilWizardTakeHit -> framecount = 4
        }
        val atlas = assets[AtlasAsset.Wizards]
        val region = atlas[wizardTexture]
        this.animation = Animation(region, framecount, 0.8f)
        this.position = Vector3(x, y, 0f)
        this.bounds = Rectangle(x, y, region.regionWidth.toFloat() / framecount, region.regionHeight.toFloat())
        log.debug { "Wizard: $wizardTexture " }
    }

    fun update(deltaTime: Float){
        log.debug { "Wizard Update" }
        print("Wizard Update ran")
        if (::animation.isInitialized) {
            animation.update(deltaTime)
        }

    }

    fun getWizard(): TextureRegion? {
        log.debug { "Wizard GetFrame" }
        return animation.getFrame()
    }
    fun getPosition(): Vector3? {
        return position
    }


    fun getBounds(): Rectangle {
        return bounds
    }



}
