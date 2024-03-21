package io.zkz.mc.gametools.settings

import io.zkz.mc.gametools.util.observable.IObservable
import net.kyori.adventure.text.Component
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KProperty

interface IGameSetting<T : Any> : IObservable<IGameSetting<T>> {
    val name: Component

    val description: Component?

    var value: T

    val displayIcon: ItemStack

    val optionIcon: ItemStack

    fun resetToDefaultValue()

    fun handleClick(clickType: ClickType) {
        when (clickType) {
            ClickType.LEFT -> handleLeftClick()
            ClickType.RIGHT -> handleRightClick()
            ClickType.DOUBLE_CLICK -> handleDoubleClick()
            ClickType.SHIFT_LEFT -> handleShiftLeftClick()
            ClickType.SHIFT_RIGHT -> handleShiftRightClick()
            else -> {}
        }
    }

    fun handleLeftClick()

    fun handleRightClick()

    fun handleDoubleClick() {
        resetToDefaultValue()
    }

    fun handleShiftLeftClick() {
        handleLeftClick()
    }

    fun handleShiftRightClick() {
        handleRightClick()
    }

    val valueAsJson: Any
        get() = value

    fun setFromJson(newValue: Any?) {
        if (newValue == null) {
            resetToDefaultValue()
            return
        }

        @Suppress("UNCHECKED_CAST")
        value = newValue as T
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value
}
