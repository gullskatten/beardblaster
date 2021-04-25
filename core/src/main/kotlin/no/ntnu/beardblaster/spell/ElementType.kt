package no.ntnu.beardblaster.spell

enum class ElementType {
    Fire,
    Ice,
    Nature;

    operator fun invoke() = toString()
}
