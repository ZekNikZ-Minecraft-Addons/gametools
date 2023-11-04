package io.zkz.mc.gametools.util

import io.zkz.mc.gametools.util.BukkitUtils.forEachPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

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
}
