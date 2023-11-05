package io.zkz.mc.gametools.inventory.opener

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.inventory.CustomUI
import io.zkz.mc.gametools.inventory.UIContents
import io.zkz.mc.gametools.inventory.item.InventoryItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

interface InventoryOpener : InjectionComponent {
    fun open(inv: CustomUI, player: Player): Inventory

    fun supports(type: InventoryType): Boolean

    fun fill(handle: Inventory, contents: UIContents) {
        val items: List<InventoryItem?> = contents.items

        for (i in items.indices) {
            if (items[i] != null) {
                handle.setItem(i, items[i]!!.itemStack)
            }
        }
    }
}
