package io.zkz.mc.gametools.inventory

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.inventory.item.ClickableItem
import io.zkz.mc.gametools.inventory.item.InventoryItem
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * A separate instance of this is created each time this inventory is opened.
 */
abstract class UIContents(private val inv: CustomUI, private val player: Player) : InjectionComponent {
    private val _items: MutableList<InventoryItem?>
    private val _paginations: MutableList<Pagination> = ArrayList()

    protected val inventoryService by inject<InventoryService>()

    init {
        _items = MutableList(slots) { null }
    }

    val items: List<InventoryItem?>
        get() = _items.toList()

    val paginations: List<Pagination>
        get() = _paginations.toList()

    protected abstract fun init()

    fun initialize() {
        init()
        _paginations.forEach(Pagination::init)
    }

    fun update() {}

    val rows: Int
        get() = inv.rows

    val cols: Int
        get() = inv.cols

    val slots: Int
        get() = rows * cols

    operator fun get(row: Int, col: Int): InventoryItem? {
        return _items[row * cols + col]
    }

    operator fun get(pos: SlotPos): InventoryItem? {
        return this[pos.row, pos.col]
    }

    operator fun set(row: Int, col: Int, item: InventoryItem): UIContents {
        if (row < 0 || row >= rows) {
            return this
        }

        if (col < 0 || col >= cols) {
            return this
        }

        _items[row * cols + col] = item

        if (!inventoryService.getOpenedPlayers(inv).contains(player)) {
            return this
        }

        val topInventory: Inventory = player.openInventory.topInventory
        topInventory.setItem(row * cols + col, item.itemStack)

        return this
    }

    operator fun set(row: Int, col: Int, stack: ItemStack): UIContents {
        return this.set(row, col, ClickableItem.of(stack))
    }

    operator fun set(pos: SlotPos, item: InventoryItem): UIContents {
        return this.set(pos.row, pos.col, item)
    }

    operator fun set(pos: SlotPos, stack: ItemStack): UIContents {
        return this.set(pos.row, pos.col, stack)
    }

    fun fill(item: InventoryItem): UIContents {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                this[row, col] = item.copy()
            }
        }

        return this
    }

    fun fillRow(row: Int, item: InventoryItem): UIContents {
        for (col in 0 until cols) {
            this[row, col] = item.copy()
        }

        return this
    }

    fun fillCol(col: Int, item: InventoryItem): UIContents {
        for (row in 0 until rows) {
            this[row, col] = item.copy()
        }

        return this
    }

    fun fillBorders(item: InventoryItem): UIContents {
        fillRow(0, item)
        fillRow(rows - 1, item)
        fillCol(0, item)
        fillCol(cols - 1, item)

        return this
    }

    fun fillRect(fromRow: Int, fromCol: Int, toRow: Int, toCol: Int, item: InventoryItem): UIContents {
        for (row in fromRow..toRow) {
            for (col in fromCol..toCol) {
                this[row, col] = item.copy()
            }
        }

        return this
    }

    fun fillRect(fromPos: SlotPos, toPos: SlotPos, item: InventoryItem): UIContents {
        return fillRect(fromPos.row, fromPos.col, toPos.row, toPos.col, item)
    }

    @JvmOverloads
    fun createIterator(type: SlotIterator.Type, startingRow: Int, startingCol: Int, reversed: Boolean = false): SlotIterator {
        return SlotIterator(type, this, reversed, startingRow, startingCol)
    }

    @JvmOverloads
    fun createIterator(type: SlotIterator.Type, startingPos: SlotPos, reversed: Boolean = false): SlotIterator {
        return createIterator(type, startingPos.row, startingPos.col, reversed)
    }

    fun createPagination(numPages: Int): Pagination {
        return this.createPagination(null, numPages)
    }

    fun createPagination(onPageChange: PaginationPageChangeHandler?, numPages: Int): Pagination {
        val pagination = Pagination(onPageChange, numPages)
        _paginations.add(pagination)
        return pagination
    }
}
