package io.zkz.mc.gametools.inventory.item

import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class InventoryItem {
    abstract val itemStack: ItemStack?

    abstract fun handleClick(event: InventoryClickEvent)

    abstract fun copy(): InventoryItem
}
