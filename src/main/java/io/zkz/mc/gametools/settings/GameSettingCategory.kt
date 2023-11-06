package io.zkz.mc.gametools.settings

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

@JvmRecord
data class GameSettingCategory(
    val name: Component,
    val description: Component,
    val displayIcon: ItemStack,
)
