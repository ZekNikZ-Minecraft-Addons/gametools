package io.zkz.mc.gametools.util

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Alias for [ItemStackBuilder].
 */
object ISB {
    fun builder(): ItemStackBuilder {
        return ItemStackBuilder.builder()
    }

    fun material(material: Material): ItemStackBuilder {
        return ItemStackBuilder.fromMaterial(material)
    }

    fun fromItemStack(stack: ItemStack): ItemStackBuilder {
        return ItemStackBuilder.fromStack(stack)
    }

    fun stack(material: Material): ItemStack {
        return ItemStack(material)
    }

    fun stack(material: Material, amount: Int): ItemStack {
        return ItemStack(material, amount)
    }
}
