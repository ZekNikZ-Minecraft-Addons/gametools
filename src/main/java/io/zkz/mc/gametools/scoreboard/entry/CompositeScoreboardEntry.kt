package io.zkz.mc.gametools.scoreboard.entry

import io.zkz.mc.gametools.scoreboard.GameScoreboard

class CompositeScoreboardEntry : ScoreboardEntry() {
    private var children: MutableList<ScoreboardEntry> = ArrayList()

    val childCount: Int
        get() = children.size

    fun addChild(entry: ScoreboardEntry): Int {
        val pos = childCount
        this.addChild(pos, entry)
        return pos
    }

    fun addChild(position: Int, entry: ScoreboardEntry) {
        children.add(position, entry)
    }

    fun removeChild(position: Int): ScoreboardEntry {
        return children.removeAt(position)
    }

    override val rowCount: Int
        get() = children.map(ScoreboardEntry::rowCount).sum()

    override fun render(pos: Int) {
        var currentPos = pos
        for (entry in children) {
            entry.render(currentPos)
            currentPos += entry.rowCount
        }
    }

    override var scoreboard: GameScoreboard
        get() = super.scoreboard
        set(scoreboard) {
            super.scoreboard = scoreboard
            children.forEach { it.scoreboard = scoreboard }
        }
}
