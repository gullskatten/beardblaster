package no.ntnu.beardblaster.commons


data class User(val displayName: String = "Stranger", var beardLength: Number = 0, override var id: String = "") : DocumentType {

    companion object {
        fun fromHashMap(data: HashMap<String, Any>): User {
            return User(
                    displayName = data.get("displayName") as String,
                    beardLength = data.get("beardLength") as Number,
                    id = data.get("id") as String
            )
        }
    }
}