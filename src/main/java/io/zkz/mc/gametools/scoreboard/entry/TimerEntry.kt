package io.zkz.mc.gametools.scoreboard.entry

import io.zkz.mc.gametools.timer.AbstractTimer
import io.zkz.mc.gametools.util.mmResolve
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

class TimerEntry(format: String, label: Component, value: AbstractTimer) : ValueEntry<AbstractTimer>(format, value) {
    private val hookId: Int
    private val label: Component

    init {
        this.label = label
        hookId = this.value.addHook { markDirty() }
    }

    override var value: AbstractTimer
        get() = super.value
        set(value) {
            throw UnsupportedOperationException("Cannot set the value of a timer entry")
        }

    override fun cleanup() {
        value.removeHook(hookId)
    }

    override fun render(pos: Int) {
        scoreboard.setLine(
            pos,
            mmResolve(
                format,
                Placeholder.component("label", label),
                Placeholder.component("value", valueComponent),
            ),
        )
    }
}
