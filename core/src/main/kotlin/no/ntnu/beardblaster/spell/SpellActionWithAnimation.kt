package no.ntnu.beardblaster.spell

import no.ntnu.beardblaster.commons.spell.SpellAction
import no.ntnu.beardblaster.sprites.WizardTextures
import no.ntnu.beardblaster.user.UserData

class SpellActionWithAnimation(spellAction: SpellAction) : SpellAction(
    spell = spellAction.spell,
    receiver = spellAction.receiver,
    caster = spellAction.caster,
) {
    init {
        super.docId = spellAction.docId
        super.damageDealt = spellAction.damageDealt
        super.damageAbsorbed = spellAction.damageAbsorbed
        super.healing = spellAction.healing
        super.receiverWizard = spellAction.receiverWizard
        super.casterWizard = spellAction.casterWizard
        super.isForfeit = spellAction.isForfeit
        determineAnimationsForSpell(spellAction)
    }

    var myWizardAnimation: WizardTextures = WizardTextures.GoodWizardIdle
    var opponentWizardAnimation: WizardTextures = WizardTextures.EvilWizardIdle

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

    override fun toString(): String {
        var text = "${casterWizard?.displayName} is casting ${spell.spellName} \n"

        if (damageAbsorbed > 0 && damageAbsorbed >= damageDealt) {
            text += "All damage was absorbed by $receiver!"
        }
        if (healing > 0) {
            text += "it healed for $healing!"
        }

        if (damageDealt > 0) {
            if (damageAbsorbed > 0) {
                text += "${casterWizard?.displayName} dealt $damageDealt damage \n ($damageAbsorbed damage was absorbed)"
            } else {
                text += "this spell dealt $damageDealt damage!"
            }
        }

        return text
    }
}
