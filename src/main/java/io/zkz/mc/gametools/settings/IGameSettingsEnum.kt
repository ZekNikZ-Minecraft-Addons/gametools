package io.zkz.mc.gametools.settings

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

interface IGameSettingsEnum {
    val label: Component

    val description: Component?

    val display: ItemStack
}