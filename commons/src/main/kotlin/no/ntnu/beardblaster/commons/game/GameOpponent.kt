package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.State
import no.ntnu.beardblaster.commons.User
import java.time.LocalDateTime
import java.time.ZoneOffset


class GameOpponent(val id: String, val beardLength: Float = 0f, val displayName: String) {
    constructor() : this("", 0f,
        "",
    )

    companion object {
        fun fromUser(user: User) : GameOpponent {
            return GameOpponent(id = user.id, beardLength = user.beardLength, displayName = user.displayName)
        }
    }
}
