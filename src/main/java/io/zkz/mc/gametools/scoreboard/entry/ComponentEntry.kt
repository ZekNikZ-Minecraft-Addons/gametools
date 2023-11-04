package io.zkz.mc.gametools.scoreboard.entry

import net.kyori.adventure.text.Component

class ComponentEntry(private val component: Component) : ScoreboardEntry() {
    override fun render(pos: Int) {
        scoreboard.setLine(pos, component)
    }
}
