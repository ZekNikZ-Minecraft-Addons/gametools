package io.zkz.mc.gametools.inventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class PaginationIterator internal constructor(
    private val iterator: SlotIterator,
    private val items: List<InventoryItemSupplier>,
    private val itemsPerPage: Int,
) {
    private val id = nextId++

    fun apply(page: Int) {
        val iter: SlotIterator = iterator.copy()
        var index = page * itemsPerPage
        var i = 0
        while (iter.hasNext() && i < itemsPerPage) {
            if (index >= items.size) {
                iter.set(ItemStack(Material.AIR))
            } else {
                iter.set(items[index].invoke())
            }
            iter.next()
            ++index
            ++i
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PaginationIterator) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

    companion object {
        private var nextId = 0
    }
}
