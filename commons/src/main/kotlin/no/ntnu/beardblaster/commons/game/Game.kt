package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.DocumentType
import java.time.LocalDateTime
import java.time.ZoneOffset

class Game(
    val code: String,
    val createdAt: Long,
    val host: GamePlayer? = null,
    val opponent: GamePlayer? = null,
    val startedAt: Long,
    val endedAt: Long,
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        "",
        LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC),
        null,
        null,
        0L,
        0L
    )

}

