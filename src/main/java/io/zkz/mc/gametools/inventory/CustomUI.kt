package io.zkz.mc.gametools.inventory

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.inventory.opener.InventoryOpener
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

// TODO: support ID and parent
class CustomUI(
    val id: String,
    val title: Component,
    val rows: Int = 6,
    val cols: Int = 9,
    val type: InventoryType = InventoryType.CHEST,
    val closeable: Boolean = true,
    val parent: CustomUI? = null,
    private val provider: (CustomUI, Player) -> UIContents,
) : InjectionComponent {
    private val inventoryService by inject<InventoryService>()

    fun open(player: Player): Inventory {
        return this.open(player, 0)
    }

    fun open(player: Player, page: Int): Inventory {
        // Remove the existing inventory if present
        val oldInv: CustomUI? = inventoryService.getInventory(player)
        oldInv?.run {
            inventoryService.setInventory(player, null)
        }

        // Create the contents
        val contents: UIContents = provider(this, player)
        contents.paginations.forEach { it.page = page }

        // Initialize the contents
        inventoryService.setContents(player, contents)
        contents.initialize()

        // Open the inventory
        val opener: InventoryOpener = inventoryService.getOpener(type)
            ?: throw IllegalStateException("No opener found for the inventory type ${type.name}")
        val handle: Inventory = opener.open(this, player)

        inventoryService.setInventory(player, this)

        return handle
    }

    fun close(player: Player) {
        inventoryService.setInventory(player, null)
        player.closeInventory()

        inventoryService.setContents(player, null)
    }
}
