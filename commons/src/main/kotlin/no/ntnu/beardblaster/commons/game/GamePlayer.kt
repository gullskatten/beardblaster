package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.user.User


open class GamePlayer(val id: String, val beardLength: Float = 0f, val displayName: String) {
    constructor() : this(
        "", 0f,
        "",
    )

    override fun toString(): String {
        return "$displayName ${beardLength}cm"
    }

    companion object {
        fun fromUser(user: User): GamePlayer {
            return GamePlayer(
                id = user.id,
                beardLength = user.beardLength,
                displayName = user.displayName
            )
        }

    }

}
