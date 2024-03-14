package io.zkz.mc.gametools.settings

import io.zkz.mc.gametools.util.observable.AbstractObservable
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

abstract class AbstractGameSetting<T : Any>(
    override val name: Component,
    override val description: Component?,
    override val displayIcon: ItemStack,
    private val defaultValue: () -> T,
    initialValue: () -> T = defaultValue,
) : AbstractObservable<IGameSetting<T>>(), IGameSetting<T> {
    override var value: T = initialValue()

    override fun resetToDefaultValue() {
        value = defaultValue()
    }
}
