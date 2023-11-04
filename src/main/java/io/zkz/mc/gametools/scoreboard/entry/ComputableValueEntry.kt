package io.zkz.mc.gametools.scoreboard.entry

import net.kyori.adventure.text.Component

class ComputableValueEntry<T>(format: String, value: () -> T) : ValueEntry<() -> T>(format, value) {
    override var value: () -> T
        get() = super.value
        set(value) = throw UnsupportedOperationException("Cannot set the value of a computable value entry")

    override val valueComponent: Component
        get() = Component.text(value().toString())
}
