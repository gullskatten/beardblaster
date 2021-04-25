package no.ntnu.beardblaster.wizard

import com.badlogic.gdx.graphics.g2d.TextureAtlas

enum class WizardTextures(val atlasKey: String) {
    EvilWizardAttack("evil_wizard_attack"),
    EvilWizardDeath("evil_wizard_death"),
    EvilWizardIdle("evil_wizard_idle"),
    EvilWizardMove("evil_wizard_move"),
    EvilWizardTakeHit("evil_wizard_take_hit"),

    GoodWizardAttack1("good_wizard_attack1"),
    GoodWizardAttack2("good_wizard_attack2"),
    GoodWizardDeath("good_wizard_death"),
    GoodWizardFall("good_wizard_fall"),
    GoodWizardHit("good_wizard_hit"),
    GoodWizardIdle("good_wizard_idle"),
    GoodWizardJump("good_wizard_jump"),
    GoodWizardRun("good_wizard_run"),
}

operator fun TextureAtlas.get(region: WizardTextures): TextureAtlas.AtlasRegion = this.findRegion(region.atlasKey)





