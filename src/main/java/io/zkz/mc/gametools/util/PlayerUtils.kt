package io.zkz.mc.gametools.util

import io.zkz.mc.gametools.util.BukkitUtils.forEachPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.util.*

object PlayerUtils {
    fun hidePlayer(plugin: Plugin, player: Player) {
        forEachPlayer { otherPlayer: Player ->
            if (otherPlayer == player) {
                return@forEachPlayer
            }
            otherPlayer.hidePlayer(plugin, player)
        }
    }

    fun showPlayer(plugin: Plugin, player: Player) {
        forEachPlayer { otherPlayer: Player ->
            if (otherPlayer == player) {
                return@forEachPlayer
            }
            otherPlayer.showPlayer(plugin, player)
        }
    }

    fun showAllPlayers(plugin: Plugin) {
        forEachPlayer { player: Player ->
            forEachPlayer inner@{ otherPlayer: Player ->
                if (otherPlayer == player) {
                    return@inner
                }
                otherPlayer.showPlayer(plugin, player)
            }
        }
    }

    fun Collection<UUID>.filterOnline(): List<UUID> {
        return this.filter { Bukkit.getPlayer(it) != null }
    }

    fun Collection<UUID>.mapOnline(): List<Player> {
        return this.mapNotNull { Bukkit.getPlayer(it) }
    }

    fun Collection<UUID>.allOnline(): Boolean {
        return this.all { Bukkit.getPlayer(it) != null }
    }

    fun Collection<UUID>.anyOnline(): Boolean {
        return this.any { Bukkit.getPlayer(it) != null }
    }
}
