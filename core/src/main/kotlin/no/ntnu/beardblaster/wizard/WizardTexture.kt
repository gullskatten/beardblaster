package no.ntnu.beardblaster.wizard

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import ktx.assets.async.AssetStorage
import ktx.log.logger
import no.ntnu.beardblaster.assets.AtlasAsset
import no.ntnu.beardblaster.assets.get

private val log = logger<WizardTexture>()
class WizardTexture {
    private lateinit var animation: Animation
    private lateinit var position: Vector3
    private lateinit var bounds: Rectangle

    fun setAnimation(x: Float, y: Float, assets: AssetStorage, wizardTexture: WizardTextures) {
        var frameCount = 0
        var cycleTime = 2f
        when (wizardTexture) {
            //Could define specific cycle times for animations as well
            WizardTextures.GoodWizardAttack2 -> frameCount = 8
            WizardTextures.GoodWizardAttack1 -> frameCount = 8
            WizardTextures.GoodWizardIdle -> frameCount = 6
            WizardTextures.GoodWizardHit -> frameCount = 4
            WizardTextures.GoodWizardDeath -> frameCount = 7
            WizardTextures.GoodWizardFall -> frameCount = 2
            WizardTextures.GoodWizardJump -> {
                frameCount = 2
                cycleTime = 3f
            }
            WizardTextures.GoodWizardRun -> frameCount = 8
            WizardTextures.EvilWizardDeath -> frameCount = 5
            WizardTextures.EvilWizardTakeHit -> frameCount = 4
            WizardTextures.EvilWizardAttack -> frameCount = 8
            WizardTextures.EvilWizardMove -> frameCount = 8
            WizardTextures.EvilWizardIdle -> frameCount = 8
        }
        val atlas = assets[AtlasAsset.Wizards]
        val region = atlas[wizardTexture]
        this.animation = Animation(region, frameCount, cycleTime)
        this.position = Vector3(x, y, 0f)
        this.bounds = Rectangle(x, y, region.regionWidth.toFloat() / frameCount, region.regionHeight.toFloat())
    }

    fun update(deltaTime: Float){
        if (::animation.isInitialized) {
            animation.update(deltaTime)
        }
    }

    fun getWizard(): TextureRegion? {
        if (::animation.isInitialized) {
        return animation.getFrame()
        }
        return null
    }

    fun getPosition(): Vector3 {
            return position
    }
    fun getBounds(): Rectangle {
            return bounds
    }
}
