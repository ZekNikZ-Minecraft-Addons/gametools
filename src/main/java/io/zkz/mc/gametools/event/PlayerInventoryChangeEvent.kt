package io.zkz.mc.gametools.event

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.inventory.PlayerInventory

class PlayerInventoryChangeEvent(
    val reason: Reason,
    val player: Player,
    val inventory: PlayerInventory,
    val event: Cancellable? = null
) : AbstractEvent(), Cancellable {
    enum class Reason {
        INVENTORY_INTERACT_GENERAL, INVENTORY_INTERACT_CLICK, INVENTORY_INTERACT_DRAG, PLAYER_ITEM_DROP, PLAYER_ITEM_CONSUME, PLAYER_ITEM_BREAK, PLAYER_ITEM_CRAFT, PLAYER_INTERACT_ENTITY, PLAYER_ITEM_PICK_UP
    }

    override fun isCancelled(): Boolean {
        return event != null && event.isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        if (event != null) {
            event.isCancelled = cancel
        }
    }
}