package io.zkz.mc.gametools.inventory

import io.zkz.mc.gametools.inventory.item.ClickableItem
import io.zkz.mc.gametools.inventory.item.InventoryItem
import org.bukkit.inventory.ItemStack

class SlotIterator internal constructor(
    private val type: Type,
    private val contents: UIContents,
    private val reversed: Boolean,
    initialRow: Int,
    initialCol: Int,
) {
    private val blacklistedRows: MutableSet<Int> = HashSet()
    private val blacklistedCols: MutableSet<Int> = HashSet()
    private val blacklistedPositions: MutableSet<SlotPos> = HashSet()
    private var nextPos: SlotPos? = null

    init {
        computeNext()
    }

    operator fun hasNext(): Boolean {
        return nextPos != null && isValid(nextPos!!)
    }

    operator fun next() {
        row = nextPos!!.row
        col = nextPos!!.col
        computeNext()
    }

    fun get(): InventoryItem? {
        return contents[row, col]
    }

    fun set(item: InventoryItem) {
        contents[row, col] = item
    }

    fun set(stack: ItemStack) {
        this.set(ClickableItem.of(stack))
    }

    var pos: SlotPos
        get() = SlotPos(row, col)
        set(value) {
            row = value.row
            col = value.col
            computeNext()
        }

    var row: Int = initialRow
        set(value) {
            field = value
            computeNext()
        }

    var col: Int = initialCol
        set(value) {
            field = value
            computeNext()
        }

    fun blacklist(row: Int, col: Int) {
        this.blacklist(SlotPos(row, col))
    }

    fun blacklist(pos: SlotPos) {
        blacklistedPositions.add(pos)
    }

    fun blacklistRow(vararg rows: Int) {
        for (row in rows) {
            blacklistedRows.add(row)
        }
    }

    fun blacklistCol(vararg cols: Int) {
        for (col in cols) {
            blacklistedCols.add(col)
        }
    }

    private fun isValid(pos: SlotPos): Boolean {
        return pos.row >= 0 && pos.row < contents.rows && pos.col >= 0 && pos.col < contents.cols
    }

    private fun computeNext() {
        var nextPos: SlotPos
        do {
            nextPos = type.next(this)
            if (!isValid(nextPos)) {
                break
            }
        } while (isBlacklisted(nextPos))
        this.nextPos = nextPos
    }

    private fun isBlacklisted(pos: SlotPos): Boolean {
        return (blacklistedPositions.contains(pos) || blacklistedRows.contains(pos.row) || blacklistedCols.contains(pos.col))
    }

    fun copy(): SlotIterator {
        val iter = SlotIterator(
            type,
            contents,
            reversed,
            row,
            col,
        )
        iter.blacklistedPositions.addAll(blacklistedPositions)
        iter.blacklistedRows.addAll(blacklistedRows)
        iter.blacklistedCols.addAll(blacklistedCols)
        return iter
    }

    enum class Type(val next: NextSlotComputer) {
        HORIZONTAL(
            { SlotPos(it.row, it.col + if (it.reversed) -1 else 1) },
        ),
        HORIZONTAL_WRAP(
            next@{ iter ->
                val newCol: Int = iter.col + if (iter.reversed) -1 else 1
                if (newCol == -1) {
                    return@next SlotPos(iter.row - 1, iter.contents.cols - 1)
                } else if (newCol == iter.contents.cols) {
                    return@next SlotPos(iter.row + 1, 0)
                }
                SlotPos(iter.row, newCol)
            },
        ),
        VERTICAL(
            { SlotPos(it.row + if (it.reversed) -1 else 1, it.col) },
        ),
        VERTICAL_WRAP(
            next@{
                val newRow: Int = it.row + if (it.reversed) -1 else 1
                if (newRow == -1) {
                    return@next SlotPos(it.contents.rows - 1, it.col - 1)
                } else if (newRow == it.contents.rows) {
                    return@next SlotPos(0, it.col + 1)
                }
                SlotPos(it.row, newRow)
            },
        ),
        ;
    }
}
