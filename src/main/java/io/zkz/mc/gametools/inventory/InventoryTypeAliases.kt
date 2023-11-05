package io.zkz.mc.gametools.inventory

import io.zkz.mc.gametools.inventory.item.InventoryItem
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

typealias InventoryClickEventHandler = (InventoryClickEvent) -> Unit
typealias ClickTypeHandler = (ClickType) -> Unit
typealias InventoryItemSupplier = () -> InventoryItem
typealias PaginationPageChangeHandler = (Int, Int) -> Unit
typealias NextSlotComputer = (SlotIterator) -> SlotPos
