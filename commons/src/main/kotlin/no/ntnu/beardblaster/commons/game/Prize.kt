package no.ntnu.beardblaster.commons.game

class Prize(
    val item: String,
    val amount: Int
) {
    constructor() : this(
        "An empty, but invaluable prize!",
        1
    )
    var receiver: String = "Unknown"
}

