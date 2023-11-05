package io.zkz.mc.gametools.inventory.opener

import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.inventory.CustomUI
import io.zkz.mc.gametools.inventory.InventoryService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

class SpecialInventoryOpener : InventoryOpener {
    private val inventoryService by inject<InventoryService>()

    override fun open(inv: CustomUI, player: Player): Inventory {
        val handle: Inventory = Bukkit.createInventory(player, inv.type, inv.title)

        fill(handle, inventoryService.getContents(player)!!)

        player.openInventory(handle)
        return handle
    }

    override fun supports(type: InventoryType): Boolean {
        return SUPPORTED.contains(type)
    }

    companion object {
        private val SUPPORTED = listOf(
            InventoryType.FURNACE,
            InventoryType.WORKBENCH,
            InventoryType.DISPENSER,
            InventoryType.DROPPER,
            InventoryType.ENCHANTING,
            InventoryType.BREWING,
            InventoryType.ANVIL,
            InventoryType.BEACON,
            InventoryType.HOPPER,
        )
    }
}
