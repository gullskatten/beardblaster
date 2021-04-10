package no.ntnu.beardblaster.commons.game

import no.ntnu.beardblaster.commons.DocumentType
import java.time.LocalDateTime

class Game (val lobbyCode: String, val createdAt: LocalDateTime, override var id: String = ""): DocumentType {
}
