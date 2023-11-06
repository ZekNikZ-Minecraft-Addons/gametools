@file:Suppress("DEPRECATION")

package io.zkz.mc.gametools.scoreboard

import io.zkz.mc.gametools.scoreboard.entry.ComponentEntry
import io.zkz.mc.gametools.scoreboard.entry.ScoreboardEntry
import io.zkz.mc.gametools.scoreboard.entry.SpaceEntry
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.RenderType
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team

/**
 * Wrapper around a Bukkit scoreboard to allow additional functionality.
 */
class GameScoreboard internal constructor(initialTitle: Component) {
    val id: Int = nextId++
    private val scoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard
    private val objective: Objective

    private var tabListObjective: Objective? = null
    private val entries: MutableList<ScoreboardEntry> = ArrayList()
    private val mappedEntries: MutableMap<String, ScoreboardEntry> = HashMap()
    private val components: MutableList<Component?> = ArrayList(15)

    var title: Component = initialTitle
        set(title) {
            field = title
            objective.displayName(title)
        }

    init {
        objective = scoreboard.registerNewObjective("display", Criteria.DUMMY, title)
        objective.displaySlot = DisplaySlot.SIDEBAR
        for (i in 0..14) {
            components.add(null)
        }
        redraw()
    }

    fun <T : ScoreboardEntry> addEntry(entry: T): T {
        if (entries.size >= 15) {
            throw IndexOutOfBoundsException("A scoreboard can only have 15 entries.")
        }
        entries.add(entry)
        entry.scoreboard = this
        redraw()
        return entry
    }

    fun <T : ScoreboardEntry> addEntry(id: String, entry: T): T {
        if (entries.size >= 15) {
            throw IndexOutOfBoundsException("A scoreboard can only have 15 entries.")
        }
        entries.add(entry)
        entry.scoreboard = this
        redraw()
        mappedEntries[id] = entry
        return entry
    }

    fun addEntry(entry: Component): ComponentEntry {
        return this.addEntry(ComponentEntry(entry))
    }

    fun redraw() {
        var pos = 0
        for (entry in entries) {
            entry.render(pos)
            pos += entry.rowCount
        }
    }

    fun getScoreboard(): Scoreboard {
        return scoreboard
    }

    fun addSpace() {
        this.addEntry(SpaceEntry())
    }

    fun setLine(pos: Int, component: Component?) {
        if (pos < 0 || pos >= 15) {
            throw IndexOutOfBoundsException("Scoreboard position must be between 0 and 15")
        }

        val existing: Component? = components[pos]
        if (existing == component) {
            return
        }

        if (component == null) {
            components[pos] = null
            scoreboard.resetScores(INVISIBLE_STRINGS[pos])
            return
        }

        components[pos] = component
        objective.getScore(INVISIBLE_STRINGS[pos]).score = 15 - pos

        var team: Team? = scoreboard.getTeam("" + pos)
        if (team == null) {
            setupTeam(pos)
            team = scoreboard.getTeam("" + pos)
            team!!.addEntry(INVISIBLE_STRINGS[pos])
        }

        team.suffix(component)
    }

    fun getEntry(id: String): ScoreboardEntry? {
        return mappedEntries[id]
    }

    private fun setupTeam(pos: Int) {
        scoreboard.getTeam("" + pos)?.unregister()
        scoreboard.registerNewTeam("" + pos)
    }

    fun removeEntry(id: String) {
        val scoreboardEntry = mappedEntries[id] ?: return

        scoreboardEntry.cleanup()
        entries.remove(scoreboardEntry)
        mappedEntries.remove(id)
        this.clear()
        redraw()
    }

    private fun clear() {
        for (i in 0..14) {
            setLine(i, null)
        }
    }

    fun swapEntry(id: String, newEntry: ScoreboardEntry): ScoreboardEntry {
        val oldEntry = mappedEntries[id] ?: throw NullPointerException("No entry with id $id")

        oldEntry.cleanup()
        val i = entries.indexOf(oldEntry)
        entries.removeAt(i)
        mappedEntries.remove(id)
        entries.add(i, newEntry)
        mappedEntries[id] = newEntry
        return oldEntry
    }

    fun unregisterTeam(team: Team) {
        team.unregister()
    }

    fun cleanup() {
        scoreboard.teams.forEach(::unregisterTeam)
        entries.forEach(ScoreboardEntry::cleanup)
    }

    fun setTabListObjective(name: String, criteria: Criteria, displayName: Component?, renderType: RenderType) {
        if (tabListObjective != null) {
            tabListObjective!!.unregister()
            tabListObjective = null
        }
        tabListObjective = scoreboard.registerNewObjective(name, criteria, displayName, renderType)
        tabListObjective!!.displaySlot = DisplaySlot.PLAYER_LIST
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameScoreboard) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    companion object {
        @Suppress("DEPRECATION")
        private val INVISIBLE_STRINGS = arrayOf(
            "\u00A70" + ChatColor.RESET,
            "\u00A71" + ChatColor.RESET,
            "\u00A72" + ChatColor.RESET,
            "\u00A73" + ChatColor.RESET,
            "\u00A74" + ChatColor.RESET,
            "\u00A75" + ChatColor.RESET,
            "\u00A76" + ChatColor.RESET,
            "\u00A77" + ChatColor.RESET,
            "\u00A78" + ChatColor.RESET,
            "\u00A79" + ChatColor.RESET,
            "\u00A7a" + ChatColor.RESET,
            "\u00A7b" + ChatColor.RESET,
            "\u00A7c" + ChatColor.RESET,
            "\u00A7d" + ChatColor.RESET,
            "\u00A7e" + ChatColor.RESET,
            "\u00A7f" + ChatColor.RESET,
        )

        private var nextId = 0
    }
}
