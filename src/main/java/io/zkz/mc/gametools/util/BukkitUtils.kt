package io.zkz.mc.gametools.util

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import java.util.*
import java.util.function.Consumer
import java.util.function.Supplier

object BukkitUtils {
    fun runNow(runnable: Runnable?) {
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("GameTools")!!, runnable!!)
    }

    fun runNextTick(runnable: Runnable?) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("GameTools")!!, runnable!!, 1)
    }

    fun dispatchEvent(event: Event?) {
        Bukkit.getServer().pluginManager.callEvent(event!!)
    }

    fun dispatchNextTick(event: Supplier<Event?>) {
        runNextTick { dispatchEvent(event.get()) }
    }

    fun forEachPlayer(func: Consumer<Player?>?) {
        Bukkit.getOnlinePlayers().forEach(func)
    }

    fun runLater(runnable: Runnable?, delay: Int) {
        Bukkit.getScheduler()
            .scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("GameTools")!!, runnable!!, delay.toLong())
    }

    fun allPlayersExcept(vararg players: Player?): Collection<Player> {
        return allPlayersExcept(Arrays.asList(*players))
    }

    fun allPlayersExcept(players: Collection<Player?>): Collection<Player> {
        val res: HashSet<out Player> = HashSet(Bukkit.getOnlinePlayers())
        players.forEach { o: Any? -> res.remove(o) }
        return res
    }
}