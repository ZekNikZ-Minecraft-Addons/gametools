package io.zkz.mc.gametools.settings.impl

import io.zkz.mc.gametools.settings.IGameSettingsEnum
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack
import kotlin.reflect.full.isSubclassOf

class EnumSetting<E : IGameSettingsEnum> private constructor(
    name: Component,
    description: Component?,
    displayIcon: ItemStack,
    options: List<Option<E>>,
    defaultValue: () -> Option<E>,
    initialValue: () -> Option<E> = defaultValue,
) : OptionSetting<E>(name, description, displayIcon, options, defaultValue, initialValue) {
    companion object {
        inline fun <reified E: IGameSettingsEnum> create(
            name: Component,
            description: Component?,
            displayIcon: ItemStack,
            noinline defaultValue: () -> E,
            noinline initialValue: (() -> E) = defaultValue,
        ): EnumSetting<E> {
            check(E::class.isSubclassOf(Enum::class)) { "Class must be an enum" }

            val options: List<Option<E>>

            return EnumSetting(
                name,
                description,
                displayIcon,
            )
        }
    }
}