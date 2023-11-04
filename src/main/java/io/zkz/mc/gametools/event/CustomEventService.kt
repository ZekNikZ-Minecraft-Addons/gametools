package io.zkz.mc.gametools.event

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.util.BukkitUtils
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import org.bukkit.event.player.PlayerItemConsumeEvent

@Injectable
class CustomEventService(
    plugin: GameToolsPlugin,
) : PluginService<GameToolsPlugin>(plugin) {
    @EventHandler
    fun onInventoryInteract(event: InventoryInteractEvent) {
        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.INVENTORY_INTERACT_GENERAL,
                event.whoClicked as Player,
                event.whoClicked.inventory,
                event,
            )
        }
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.INVENTORY_INTERACT_CLICK,
                event.whoClicked as Player,
                event.whoClicked.inventory,
                event,
            )
        }
    }

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.INVENTORY_INTERACT_DRAG,
                event.whoClicked as Player,
                event.whoClicked.inventory,
                event,
            )
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.PLAYER_ITEM_DROP,
                event.player,
                event.player.inventory,
                event,
            )
        }
    }

    @EventHandler
    fun onPlayerItemConsume(event: PlayerItemConsumeEvent) {
        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.PLAYER_ITEM_CONSUME,
                event.player,
                event.player.inventory,
                event,
            )
        }
    }

    @EventHandler
    fun onPlayerItemBreak(event: PlayerItemBreakEvent) {
        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.PLAYER_ITEM_BREAK,
                event.player,
                event.player.inventory,
            )
        }
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.PLAYER_ITEM_CRAFT,
                event.whoClicked as Player,
                event.whoClicked.inventory,
                event,
            )
        }
    }

    @EventHandler
    fun onUseBucket(event: PlayerInteractAtEntityEvent) {
        if (event.player.inventory.itemInMainHand.type != Material.BUCKET && event.player.inventory.itemInMainHand.type != Material.WATER_BUCKET) {
            return
        }

        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.PLAYER_INTERACT_ENTITY,
                event.player,
                event.player.inventory,
                event,
            )
        }
    }

    @EventHandler
    fun onEntityPickupItem(event: EntityPickupItemEvent) {
        if (event.entity !is Player) {
            return
        }

        BukkitUtils.dispatchNextTick {
            PlayerInventoryChangeEvent(
                PlayerInventoryChangeEvent.Reason.PLAYER_ITEM_PICK_UP,
                event.entity as Player,
                (event.entity as Player).inventory,
                event,
            )
        }
    }
}
