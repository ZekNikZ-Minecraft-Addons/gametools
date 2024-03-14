package io.zkz.mc.gametools.settings

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

interface IGameSettingOption {
    val label: Component

    val description: Component?

    val icon: ItemStack
}
