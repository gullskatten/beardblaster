package no.ntnu.beardblaster.spell

import no.ntnu.beardblaster.commons.spell.SpellAction
import no.ntnu.beardblaster.sprites.WizardTextures
import no.ntnu.beardblaster.user.UserData

class SpellActionWithAnimation(spellAction: SpellAction) :
    SpellAction(spellAction.spell, spellAction.caster, spellAction.receiver) {

    init {
        determineAnimationsForSpell(spellAction)
    }

    private var myWizardAnimation: WizardTextures = WizardTextures.GoodWizardIdle
    private var opponentWizardAnimation: WizardTextures = WizardTextures.EvilWizardIdle

    private fun determineAnimationsForSpell(spellAction: SpellAction) {
        if (spellAction.receiverWizard!!.isWizardDefeated() || spellAction.casterWizard!!.isWizardDefeated()) {
            if (spellAction.receiverWizard!!.isWizardDefeated()) {
                if (spellAction.receiver == UserData.instance.user!!.id) {
                    myWizardAnimation = WizardTextures.GoodWizardDeath
                    opponentWizardAnimation = WizardTextures.EvilWizardIdle
                } else {
                    myWizardAnimation = WizardTextures.GoodWizardJump
                    opponentWizardAnimation = WizardTextures.EvilWizardDeath
                }
            }
            if (spellAction.casterWizard!!.isWizardDefeated()) {
                if (spellAction.caster == UserData.instance.user!!.id) {
                    myWizardAnimation = WizardTextures.GoodWizardDeath
                    opponentWizardAnimation = WizardTextures.EvilWizardIdle
                } else {
                   myWizardAnimation = WizardTextures.GoodWizardJump
                   opponentWizardAnimation = WizardTextures.EvilWizardDeath
                }
            }
        } else if (spellAction.damageDealt > 0) {
            if (spellAction.receiver == UserData.instance.user!!.id) {
                myWizardAnimation = WizardTextures.GoodWizardHit
                opponentWizardAnimation = WizardTextures.EvilWizardAttack
            } else {
                myWizardAnimation = WizardTextures.GoodWizardAttack1
                opponentWizardAnimation = WizardTextures.EvilWizardTakeHit
            }
        } else if (spellAction.healing > 0 && spellAction.damageDealt == 0) {
            if (spellAction.receiver == UserData.instance.user!!.id) {
                myWizardAnimation = WizardTextures.GoodWizardIdle
                opponentWizardAnimation = WizardTextures.EvilWizardIdle
            } else {
                myWizardAnimation = WizardTextures.GoodWizardJump
                opponentWizardAnimation = WizardTextures.EvilWizardIdle
            }
        } else if (spellAction.damageAbsorbed > 0) {
            if (spellAction.receiver == UserData.instance.user!!.id) {
               myWizardAnimation = WizardTextures.GoodWizardIdle
               opponentWizardAnimation = WizardTextures.EvilWizardIdle
            } else {
                myWizardAnimation = WizardTextures.GoodWizardJump
                opponentWizardAnimation = WizardTextures.EvilWizardIdle
            }
        }
    }
    }
