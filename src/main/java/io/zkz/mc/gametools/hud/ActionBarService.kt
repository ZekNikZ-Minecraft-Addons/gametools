package io.zkz.mc.gametools.hud

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.service.PluginService
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitTask
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Injectable
class ActionBarService(plugin: GameToolsPlugin) : PluginService<GameToolsPlugin>(plugin) {
    private var task: BukkitTask? = null
    private var index = 0
    private var delay = 0
    private val messageOrder: MutableMap<UUID, MutableList<String>> = HashMap()
    private val messages: MutableMap<UUID, MutableMap<String, Component>> = HashMap()

    override fun onEnable() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable { displayActionBarMessages() }, 1, 1)
    }

    override fun onDisable() {
        task?.cancel()
        task = null
    }

    fun addMessage(playerId: UUID, key: String, message: Component) {
        if (!messages.containsKey(playerId)) {
            messages[playerId] = HashMap()
            messageOrder[playerId] = ArrayList()
        }
        messages[playerId]!![key] = message
        if (!messageOrder[playerId]!!.contains(key)) {
            messageOrder[playerId]!!.add(key)
        }
    }

    fun removeMessage(playerId: UUID, key: String) {
        if (!messages.containsKey(playerId)) {
            return
        }
        messages[playerId]!!.remove(key)
        messageOrder[playerId]!!.remove(key)
        if (messages[playerId]!!.isEmpty()) {
            messages.remove(playerId)
            messageOrder.remove(playerId)
        }
    }

    private fun displayActionBarMessages() {
        messages.keys.forEach {
            val player = Bukkit.getPlayer(it) ?: return@forEach
            player.sendActionBar(messages[it]!![messageOrder[it]!![index % messageOrder[it]!!.size]]!!)
        }
        if (delay > 0) {
            --delay
        } else {
            ++index
            delay = 30
        }
    }
}
