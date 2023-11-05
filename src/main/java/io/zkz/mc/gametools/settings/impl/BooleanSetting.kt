package io.zkz.mc.gametools.settings.impl

import io.zkz.mc.gametools.settings.AbstractGameSetting
import io.zkz.mc.gametools.util.ISB.material
import io.zkz.mc.gametools.util.mm
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class BooleanSetting(
    name: Component,
    description: Component?,
    displayIcon: ItemStack,
    defaultValue: () -> Boolean,
    initialValue: () -> Boolean = defaultValue,
) : AbstractGameSetting<Boolean>(name, description, displayIcon, defaultValue, initialValue) {
    override val optionIcon: ItemStack
        get() = if (value) TRUE_ICON else FALSE_ICON

    override fun handleLeftClick() = toggleValue()

    override fun handleRightClick() = toggleValue()

    private fun toggleValue() {
        value = !value
    }

    companion object {
        private val TRUE_ICON = material(Material.LIME_DYE)
            .name(mm("<lime>Enabled"))
            .build()
        private val FALSE_ICON = material(Material.GRAY_DYE)
            .name(mm("<red>Disabled"))
            .build()
    }
}