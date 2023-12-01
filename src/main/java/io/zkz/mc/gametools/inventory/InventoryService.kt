package io.zkz.mc.gametools.inventory

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.inventory.opener.ChestInventoryOpener
import io.zkz.mc.gametools.inventory.opener.InventoryOpener
import io.zkz.mc.gametools.inventory.opener.SpecialInventoryOpener
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.util.BukkitUtils.runNow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.scheduler.BukkitRunnable

@Injectable
class InventoryService(plugin: GameToolsPlugin) : PluginService<GameToolsPlugin>(plugin) {
    private val inventories: MutableMap<Player, CustomUI> = HashMap()
    private val contents: MutableMap<Player, UIContents> = HashMap()
    private val openers: MutableList<InventoryOpener> = ArrayList()

    fun getInventory(player: Player): CustomUI? {
        return inventories[player]
    }

    fun setInventory(player: Player, inv: CustomUI?) {
        if (inv == null) {
            inventories.remove(player)
        } else {
            inventories[player] = inv
        }
    }

    fun getContents(player: Player): UIContents? {
        return contents[player]
    }

    fun setContents(player: Player, contents: UIContents?) {
        if (contents == null) {
            this.contents.remove(player)
        } else {
            this.contents[player] = contents
        }
    }

    fun getOpener(type: InventoryType): InventoryOpener? {
        var opener = openers.firstOrNull { it.supports(type) }
        if (opener == null) {
            opener = DEFAULT_OPENERS.firstOrNull { it.supports(type) }
        }
        return opener
    }

    fun registerOpeners(vararg openers: InventoryOpener) {
        this.openers.addAll(listOf(*openers))
    }

    fun getOpenedPlayers(inv: CustomUI): List<Player> {
        return inventories.entries
            .filter { (_, value) ->
                inv == value
            }
            .map { (player) -> player }
    }

    override fun onEnable() {
        InvTask().runTaskTimer(plugin, 1, 1)
    }

    @EventHandler(priority = EventPriority.LOW)
    private fun onInventoryClick(e: InventoryClickEvent) {
        val p: Player = e.whoClicked as Player

        if (!inventories.containsKey(p)) return

        if (e.action == InventoryAction.COLLECT_TO_CURSOR || e.action == InventoryAction.MOVE_TO_OTHER_INVENTORY || e.action == InventoryAction.NOTHING) {
            e.isCancelled = true
        }

        if (e.clickedInventory === p.openInventory.topInventory) {
            e.isCancelled = true

            if (e.slot < 0) {
                return
            }

            val inv = inventories[p]!!

            val row: Int = e.slot / inv.cols
            val column: Int = e.slot % inv.cols

            if (row >= inv.rows || column >= inv.cols) return

            contents[p]!![row, column]?.handleClick(e)

            p.updateInventory()
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private fun onInventoryDrag(e: InventoryDragEvent) {
        val p: Player = e.whoClicked as Player

        if (!inventories.containsKey(p)) return

//        val inv = inventories[p]!!

        for (slot in e.rawSlots) {
            if (slot >= p.openInventory.topInventory.size) continue
            e.isCancelled = true
            break
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private fun onInventoryOpen(e: InventoryOpenEvent) {
        val p: Player = e.player as Player

        if (!inventories.containsKey(p)) return

//        val inv = inventories[p]!!
    }

    @EventHandler(priority = EventPriority.LOW)
    private fun onInventoryClose(e: InventoryCloseEvent) {
        val p: Player = e.player as Player

        if (!inventories.containsKey(p)) return

        val inv = inventories[p]!!

        if (inv.closeable) {
            e.inventory.clear()
            inventories.remove(p)
            contents.remove(p)
        } else {
            runNow { p.openInventory(e.inventory) }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private fun onPlayerQuit(e: PlayerQuitEvent) {
        val p: Player = e.player

        if (!inventories.containsKey(p)) return

//        val inv = inventories[p]!!

        inventories.remove(p)
        contents.remove(p)
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onPluginDisable(@Suppress("UNUSED_PARAMETER") e: PluginDisableEvent) {
        inventories.forEach { (player, inv) -> inv.close(player) }

        inventories.clear()
        contents.clear()
    }

    internal inner class InvTask : BukkitRunnable() {
        override fun run() {
            contents.values.forEach(UIContents::update)
        }
    }

    companion object {
        private val DEFAULT_OPENERS = listOf(
            ChestInventoryOpener(),
            SpecialInventoryOpener(),
        )
    }
}
