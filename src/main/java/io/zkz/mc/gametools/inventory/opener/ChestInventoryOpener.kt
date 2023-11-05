package io.zkz.mc.gametools.inventory.opener

import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.inventory.CustomUI
import io.zkz.mc.gametools.inventory.InventoryService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

class ChestInventoryOpener : InventoryOpener {
    private val inventoryService by inject<InventoryService>()

    override fun open(inv: CustomUI, player: Player): Inventory {
        require(inv.cols == 9) { "The column count for the chest inventory must be 9, found: ${inv.cols}." }
        require(inv.rows in 1..6) { "The row count for the chest inventory must be between 1 and 6, found: ${inv.rows}." }

        val handle: Inventory = Bukkit.createInventory(player, inv.rows * inv.cols, inv.title)

        fill(handle, inventoryService.getContents(player)!!)

        player.openInventory(handle)
        return handle
    }

    override fun supports(type: InventoryType): Boolean {
        return type == InventoryType.CHEST || type == InventoryType.ENDER_CHEST
    }
}
