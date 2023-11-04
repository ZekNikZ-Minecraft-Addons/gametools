package io.zkz.mc.gametools.readyup

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.mm
import io.zkz.mc.gametools.hud.ActionBarService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap


class ReadyUpSession internal constructor(
    private val sessionId: Int,
    players: Collection<UUID>,
    private val onAllReady: Runnable,
    private val onPlayerReady: ((Player, ReadyUpSession) -> Unit)?
) : InjectionComponent {
    private val readyPlayers: MutableMap<UUID, Boolean> = ConcurrentHashMap()
    private val bossBar: BossBar

    private val actionBarService by inject<ActionBarService>()
    private val readyUpService by inject<ReadyUpService>()

    init {
        players.forEach { playerId -> readyPlayers[playerId] = false }
        bossBar = Bukkit.createBossBar("Ready Up: 0/" + players.size + " players ready", BarColor.GREEN, BarStyle.SOLID)
        updateBossbar()
        bossBar.isVisible = true
        players
            .mapNotNull(Bukkit::getPlayer)
            .forEach(bossBar::addPlayer)
    }

    val readyPlayerCount: Long
        get() = readyPlayers.values.count { it }.toLong()

    val totalPlayerCount: Long
        get() = readyPlayers.size.toLong()

    fun cancel() {
        readyUpService.cleanupSession(sessionId)
        bossBar.removeAll()
        readyPlayers.keys.forEach { actionBarService.removeMessage(it, "ready") }
    }

    fun complete() {
        onAllReady.run()
        cancel()
    }

    /**
     * Mark a player as ready
     *
     * @param player the player
     * @return whether the player was marked as ready (= whether the player was tracked and not already ready)
     */
    fun markPlayerAsReady(player: Player): Boolean {
        if (!isPlayerTracked(player) || (readyPlayers[player.uniqueId] == true)) {
            return false
        }
        readyPlayers[player.uniqueId] = true

        // Player ready callback
        onPlayerReady?.invoke(player, this)

        // Chat message
        readyPlayers.keys
            .mapNotNull(Bukkit::getPlayer)
            .forEach {
                // Don't send chat message to self
                if (player == it) {
                    return@forEach
                }
                Chat.sendMessage(it, ChatType.PASSIVE_INFO, mm("<0> is ready!", player.displayName()))
            }

        // Remove ready message
        actionBarService.removeMessage(player.uniqueId, "ready")
        updateBossbar()

        // Check if this was the last player
        if (readyPlayerCount == totalPlayerCount) {
            complete()
        }
        return true
    }

    fun isPlayerTracked(player: Player): Boolean {
        return readyPlayers.containsKey(player.uniqueId)
    }

    fun undoReady(playerId: UUID): Boolean {
        readyPlayers[playerId] = false
        updateBossbar()
        return true
    }

    val readyPlayerDisplayNames: List<Component>
        get() = readyPlayers.entries
            .filter { it.value }
            .map { (playerId) ->
                val player: Player? = Bukkit.getPlayer(playerId)
                if (player != null) {
                    return@map player.displayName()
                }
                mm("<dark_red>" + Bukkit.getOfflinePlayer(playerId).name + " (offline)")
            }

    val readyPlayerNames: List<Component>
        get() {
            return readyPlayers.entries
                .filter { it.value }
                .map { (playerId) ->
                    val player: Player? = Bukkit.getPlayer(playerId)
                    if (player != null) {
                        return@map player.name()
                    }
                    mm(Bukkit.getOfflinePlayer(playerId).name ?: "<unknown>")
                }
        }

    val notReadyPlayerDisplayNames: List<Component>
        get() {
            return readyPlayers.entries
                .filter { it.value }
                .map { (playerId) ->
                    val player: Player? = Bukkit.getPlayer(playerId)
                    if (player != null) {
                        return@map player.displayName()
                    }
                    mm("<dark_red>" + Bukkit.getOfflinePlayer(playerId).getName() + " (offline)")
                }
        }

    private fun updateBossbar() {
        bossBar.setTitle("Ready Up: $readyPlayerCount/$totalPlayerCount players ready")
        bossBar.progress = readyPlayerCount.toDouble() / totalPlayerCount
    }
}