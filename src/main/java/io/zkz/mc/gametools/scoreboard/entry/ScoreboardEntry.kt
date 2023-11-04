package io.zkz.mc.gametools.scoreboard.entry

import io.zkz.mc.gametools.scoreboard.GameScoreboard

abstract class ScoreboardEntry {
    private val thisId = nextId++
    private var _scoreboard: GameScoreboard? = null
    abstract fun render(pos: Int)

    open val rowCount: Int
        get() = 1

    protected fun markDirty() {
        scoreboard.redraw()
    }

    open var scoreboard: GameScoreboard
        get() {
            checkNotNull(_scoreboard) { "This entry is not part of a scoreboard" }

            return _scoreboard!!
        }
        set(scoreboard) {
            _scoreboard = scoreboard
        }

    open fun cleanup() = Unit

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScoreboardEntry) return false

        if (thisId != other.thisId) return false

        return true
    }

    override fun hashCode(): Int {
        return thisId
    }

    companion object {
        private var nextId = 0
    }
}
