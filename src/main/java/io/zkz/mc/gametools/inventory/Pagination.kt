package io.zkz.mc.gametools.inventory

import io.zkz.mc.gametools.inventory.item.InventoryItem

class Pagination internal constructor(private val onPageChange: PaginationPageChangeHandler?, initialNumPages: Int) {
    private val paginationIterators: MutableSet<PaginationIterator> = HashSet()

    fun init() {
        onPageChange?.invoke(0, 0)
    }

    var page: Int = 0
        set(page) {
            if (page < 0 || page >= numPages) {
                return
            }

            val oldPage = field
            field = page

            paginationIterators.forEach { it.apply(this.page) }
            onPageChange?.invoke(oldPage, page)
        }

    var numPages: Int = initialNumPages
        set(value) {
            field = value

            if (page >= numPages) {
                page = numPages - 1
            }

            page = page
        }

    fun createIterator(iterator: SlotIterator, items: List<InventoryItemSupplier>, itemsPerPage: Int): PaginationIterator {
        val iter = PaginationIterator(iterator, items, itemsPerPage)
        paginationIterators.add(iter)
        iter.apply(page)
        return iter
    }

    fun createIterator(iterator: SlotIterator, itemsPerPage: Int, items: List<InventoryItem>): PaginationIterator {
        return createIterator(iterator, items.map { { it } }, itemsPerPage)
    }

    fun removeIterator(iterator: PaginationIterator) {
        paginationIterators.remove(iterator)
    }

    fun prev() {
        page--
    }

    fun next() {
        page++
    }
}
