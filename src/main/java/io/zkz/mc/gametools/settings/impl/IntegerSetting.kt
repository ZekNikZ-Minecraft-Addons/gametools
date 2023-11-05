package io.zkz.mc.gametools.settings.impl

import io.zkz.mc.gametools.settings.AbstractGameSetting
import io.zkz.mc.gametools.util.ISB
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min


class IntegerSetting(
    name: Component,
    description: Component?,
    displayIcon: ItemStack,
    private val min: () -> Int,
    private val max: () -> Int,
    private val step: () -> Int,
    defaultValue: () -> Int,
    initialValue: () -> Int = defaultValue,
) : AbstractGameSetting<Int>(name, description, displayIcon, defaultValue, initialValue) {
    override val optionIcon: ItemStack
        get() = ISB.material(Material.LIGHT_BLUE_DYE)
            .name(Component.text(value))
            .build()

    override var value: Int
        get() = super.value
        set(value) {
            super.value = clamp(value, min(), max())
        }

    override fun handleLeftClick() {
        value -= step()
    }

    override fun handleShiftLeftClick() {
        value -= step() * 5
    }

    override fun handleRightClick() {
        value += step()
    }

    override fun handleShiftRightClick() {
        value += step() * 5
    }

    companion object {
        private fun clamp(value: Int, min: Int, max: Int): Int {
            return max(min(value, max), min)
        }
    }
}