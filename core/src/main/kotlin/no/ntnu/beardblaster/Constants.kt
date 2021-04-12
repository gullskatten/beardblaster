package no.ntnu.beardblaster

enum class ElementType {
    Fire,
    Ice,
    Nature;

    operator fun invoke() = toString()
}
