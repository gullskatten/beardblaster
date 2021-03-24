package no.ntnu.beardblaster.commons


data class User(val displayName: String = "Stranger", var beardLength: Number = 0, override var id: String = "") : DocumentType {
    //override fun <User> fromHashMap(hashmap: HashMap<String, Any>) {
    //    TODO("Not yet implemented")
    //}

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