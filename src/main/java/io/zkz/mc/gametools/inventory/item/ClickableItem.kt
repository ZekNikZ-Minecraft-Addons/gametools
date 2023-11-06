package io.zkz.mc.gametools.inventory.item

import io.zkz.mc.gametools.inventory.ClickTypeHandler
import io.zkz.mc.gametools.inventory.InventoryClickEventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class ClickableItem private constructor(override val itemStack: ItemStack, clickHandler: InventoryClickEventHandler) : InventoryItem() {
    private val clickHandler: InventoryClickEventHandler

    init {
        this.clickHandler = clickHandler
    }

    override fun handleClick(event: InventoryClickEvent) {
        clickHandler(event)
    }

    override fun copy(): InventoryItem {
        return ClickableItem(itemStack.clone(), clickHandler)
    }

    companion object {
        fun of(stack: ItemStack): ClickableItem {
            return ClickableItem(stack.clone()) {}
        }

        fun ofEvent(stack: ItemStack, clickHandler: InventoryClickEventHandler): ClickableItem {
            return ClickableItem(stack, clickHandler)
        }

        fun of(stack: ItemStack, clickHandler: ClickTypeHandler): ClickableItem {
            return ClickableItem(stack) { clickHandler(it.click) }
        }
    }
}
