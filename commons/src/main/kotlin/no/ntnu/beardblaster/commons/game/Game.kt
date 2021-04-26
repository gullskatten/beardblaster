package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.user.DocumentType
import no.ntnu.beardblaster.commons.wizard.Wizard
import java.time.LocalDateTime
import java.time.ZoneOffset

class Game(
    val code: String,
    val createdAt: Long,
    val host: GamePlayer? = null,
    val opponent: GamePlayer? = null,
    val winner: Wizard? = null,
    val loser: Wizard? = null,
    val startedAt: Long,
    val endedAt: Long,
    var loot: List<Loot>,
    var isDraw: Boolean?,
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        "",
        LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC),
        null,
        null,
        null,
        null,
        0L,
        0L,
        emptyList(),
        false
    )

}

