package io.zkz.mc.gametools.scoreboard.entry

import io.zkz.mc.gametools.util.mm

class SpaceEntry : ScoreboardEntry() {
    override fun render(pos: Int) {
        scoreboard.setLine(pos, mm(""))
    }
}
