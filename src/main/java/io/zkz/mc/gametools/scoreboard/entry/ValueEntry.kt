package io.zkz.mc.gametools.scoreboard.entry

import io.zkz.mc.gametools.util.mmResolve
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

open class ValueEntry<T>(
    protected val format: String,
    initialValue: T,
) : ScoreboardEntry() {
    open var value: T = initialValue
        set(newValue) {
            field = newValue
            markDirty()
        }

    protected open val valueComponent: Component
        get() = Component.text(value.toString())

    override fun render(pos: Int) {
        scoreboard.setLine(pos, mmResolve(format, Placeholder.component("value", valueComponent)))
    }
}
