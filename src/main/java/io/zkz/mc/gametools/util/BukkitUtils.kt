package io.zkz.mc.gametools.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.*

object BukkitUtils {
    fun runNow(runnable: Runnable) {
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("GameTools")!!, runnable)
    }

    fun runNextTick(runnable: Runnable) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("GameTools")!!, runnable, 1)
    }

    fun dispatchEvent(event: Event) {
        Bukkit.getServer().pluginManager.callEvent(event)
    }

    fun dispatchNextTick(event: () -> Event) {
        runNextTick { dispatchEvent(event()) }
    }

    fun forEachPlayer(func: (Player) -> Unit) {
        Bukkit.getOnlinePlayers().forEach(func)
    }

    fun runLater(runnable: Runnable, delay: Int) {
        Bukkit.getScheduler()
            .scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("GameTools")!!, runnable, delay.toLong())
    }

    fun allPlayersExcept(vararg players: Player): Collection<Player> {
        return allPlayersExcept(listOf(*players))
    }

    fun allPlayersExcept(players: Collection<Player>): Collection<Player> {
        val res: HashSet<out Player> = HashSet(Bukkit.getOnlinePlayers())
        players.forEach { res.remove(it) }
        return res
    }
}
