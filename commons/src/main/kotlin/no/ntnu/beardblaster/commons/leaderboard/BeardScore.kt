package no.ntnu.beardblaster.commons.leaderboard

import no.ntnu.beardblaster.commons.user.DocumentType

class BeardScore(
    var beardLength: Float = 0f,
    var displayName: String = "",
    override var id: String = ""
) : DocumentType {
    constructor() : this(
        0f,
        "",
        ""
    )

}
