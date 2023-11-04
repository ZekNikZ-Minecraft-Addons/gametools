package io.zkz.mc.gametools.readyup

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.hud.ActionBarService
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.sound.StandardSounds
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.ComponentUtils.join
import io.zkz.mc.gametools.util.mm
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Injectable
class ReadyUpService(
    plugin: GameToolsPlugin,
    private val actionBarService: ActionBarService
) : PluginService<GameToolsPlugin>(plugin) {
    private var nextId = 0
    private val sessions: MutableMap<Int, ReadyUpSession> = ConcurrentHashMap()

    @JvmOverloads
    fun waitForReady(players: Collection<UUID>, onAllReady: Runnable, onPlayerReady: ((Player, ReadyUpSession) -> Unit)? = null): Int {
        val id = nextId++
        val session = ReadyUpSession(id, players, onAllReady, onPlayerReady)
        sessions[id] = session
        players.forEach { uuid: UUID ->
            val player: Player? = Bukkit.getPlayer(uuid)
            if (player != null) {
                displayInitialReadyMessage(player)
            }
            actionBarService.addMessage(uuid, "ready", mm("<alert_accent>Are you ready? Type <alert_info>/ready</alert_info> to confirm."))
        }
        return id
    }

    fun cancelReadyWait(id: Int, runHandler: Boolean) {
        val session = sessions[id]
        if (session != null) {
            if (runHandler) {
                session.complete()
            } else {
                session.cancel()
            }
        }
    }

    /**
     * Called by ReadyUpSession#cancel()
     * @param id the session id
     */
    fun cleanupSession(id: Int) {
        sessions.remove(id)
    }

    fun recordReady(player: Player): Boolean {
        var success = false
        for (session in sessions.values) {
            success = success or session.markPlayerAsReady(player)
        }
        return success
    }

    fun undoReady(playerId: UUID): Boolean {
        var success = false
        for (session in sessions.values) {
            success = success or session.undoReady(playerId)
        }
        return success
    }

    private fun displayInitialReadyMessage(player: Player) {
        Chat.sendMessage(player, ChatType.GAME_INFO, mm("Are you ready? Type <alert_info>/ready</alert_info> to confirm."))
        player.playSound(player.location, StandardSounds.ALERT_INFO, 1f, 1f)
    }

    @EventHandler
    private fun onJoin(event: PlayerJoinEvent) {
        if (sessions.values.stream().anyMatch { s: ReadyUpSession -> s.isPlayerTracked(event.player) }) {
            displayInitialReadyMessage(event.player)
        }
    }

    fun sendStatus(sender: Audience?) {
        sessions.forEach { (id: Int?, session: ReadyUpSession) ->
            Chat.sendMessage(sender!!, mm("<gold>Session <0>:", id))
            Chat.sendMessage(sender, mm("<green> - ready: <0>", mm(", ").join(session.readyPlayerDisplayNames)))
            Chat.sendMessage(sender, mm("<red> - not ready: <0>", mm(", ").join(session.notReadyPlayerDisplayNames)))
        }
    }

    val allReadyPlayerNames: Set<String>
        get() = sessions.values
            .flatMap { it.readyPlayerNames.map(PlainTextComponentSerializer.plainText()::serialize) }
            .toSet()

    fun getSessions(): Map<Int, ReadyUpSession> {
        return sessions
    }
}
