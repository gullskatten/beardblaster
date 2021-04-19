package no.ntnu.beardblaster.sprites

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Array
import ktx.log.logger
import no.ntnu.beardblaster.screen.GameplayScreen

private val log = logger<GameplayScreen>()
class Animation {
    private var frames: Array<TextureRegion>? = Array()
    private var maxFrameTime: Float
    private var currentFrameTime: Float = 0.0f
    private var frameCount: Int
    private var frame: Int

    constructor(region: TextureRegion, frameCount: Int, cycleTime: Float) {
        frames = Array<TextureRegion>()
        val frameWidth = region.regionWidth / frameCount
        for (i in 0..frameCount) {
            frames!!.add(TextureRegion(region, i * frameWidth, 0, frameWidth, region.regionHeight))
        }
        this.frameCount = frameCount
        maxFrameTime = cycleTime / frameCount
        frame = 0
    }



    fun update(deltaTime: Float) {
        currentFrameTime += deltaTime
        if (currentFrameTime > maxFrameTime) {
            frame++
            currentFrameTime = 0f
        }
        if (frame >= frameCount) {
            frame = 0
        }
    }

    fun getFrame(): TextureRegion? {
        return frames?.get(frame)
    }
}
