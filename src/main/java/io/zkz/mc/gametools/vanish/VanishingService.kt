package io.zkz.mc.gametools.vanish

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.util.BukkitUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

@Injectable
class VanishingService(plugin: GameToolsPlugin) : PluginService<GameToolsPlugin>(plugin) {
    /**
     * Map of player to players they cannot see.
     */
    private val _hiddenPlayers: MutableMap<UUID, MutableSet<String>> = HashMap()

    val hiddenPlayers: Set<UUID>
        get() = _hiddenPlayers.keys

    /**
     * Check if player can see target.
     */
    fun canSee(player: Player): Boolean {
        return canSee(player.uniqueId)
    }

    /**
     * Check if player can see target.
     */
    fun canSee(playerId: UUID): Boolean {
        return !_hiddenPlayers.containsKey(playerId)
    }

    fun hidePlayer(player: Player, reason: String) {
        hidePlayer(player.uniqueId, reason)
    }

    fun hidePlayer(playerId: UUID, reason: String) {
        _hiddenPlayers.computeIfAbsent(playerId) { mutableSetOf() }
        _hiddenPlayers[playerId]!!.add(reason)
        if (_hiddenPlayers.containsKey(playerId)) {
            val player: Player? = Bukkit.getPlayer(playerId)
            if (player != null) {
                BukkitUtils.forEachPlayer { it.hidePlayer(plugin, player) }
            }
        }
    }

    fun showPlayer(player: Player, reason: String) {
        showPlayer(player.uniqueId, reason)
    }

    fun showPlayer(playerId: UUID, reason: String) {
        if (!_hiddenPlayers.containsKey(playerId)) {
            return
        }
        _hiddenPlayers[playerId]!!.remove(reason)
        if (_hiddenPlayers[playerId]!!.isEmpty()) {
            _hiddenPlayers.remove(playerId)
            val player: Player? = Bukkit.getPlayer(playerId)
            if (player != null) {
                BukkitUtils.forEachPlayer { it.showPlayer(plugin, player) }
            }
        }
    }

    fun togglePlayer(player: Player, reason: String) {
        this.togglePlayer(player.uniqueId, reason)
    }

    fun togglePlayer(playerId: UUID, reason: String) {
        if (canSee(playerId)) {
            hidePlayer(playerId, reason)
        } else {
            showPlayer(playerId, reason)
        }
    }

    fun getPlayerHiddenReasons(player: Player): Set<String>? {
        return getPlayerHiddenReasons(player.uniqueId)
    }

    fun getPlayerHiddenReasons(playerId: UUID): Set<String>? {
        return _hiddenPlayers[playerId]
    }
}
