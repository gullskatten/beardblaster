package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.user.DocumentType
import java.time.LocalDateTime
import java.time.ZoneOffset

class Game(
    val code: String,
    val createdAt: Long,
    val opponent: GameOpponent? = null,
    val started: Long,
    val ended: Long,
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        "", LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC),
        null,
        0L,
        0L
    )

}

