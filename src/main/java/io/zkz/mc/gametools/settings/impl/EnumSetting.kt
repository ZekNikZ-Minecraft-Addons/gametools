package io.zkz.mc.gametools.settings.impl

import io.zkz.mc.gametools.settings.IGameSettingOption
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

class EnumSetting<E : Enum<E>>(
    name: Component,
    description: Component?,
    displayIcon: ItemStack,
    options: List<Option<E>>,
    defaultValue: () -> Option<E>,
    initialValue: () -> Option<E> = defaultValue,
) : OptionSetting<E>(
    name,
    description,
    displayIcon,
    options,
    defaultValue,
    initialValue,
) {
    override val valueAsJson: String
        get() = value.name

    override fun setFromJson(newValue: Any?) {
        if (newValue is String?) {
            if (newValue == null) {
                resetToDefaultValue()
                return
            }

            value = options.first { it.value.name == newValue }.value
        } else {
            throw IllegalArgumentException("Cannot set enum setting to non-string value")
        }
    }

    companion object {
        inline fun <reified E : Enum<E>> from(
            name: Component,
            description: Component?,
            displayIcon: ItemStack,
            noinline defaultValue: () -> E,
            noinline initialValue: () -> E = defaultValue,
        ): EnumSetting<E> {
            val options = enumValues<E>().map(::makeOption)

            val defaultVal = defaultValue()
            val default = options.first { it == defaultVal }

            val initialVal = defaultValue()
            val initial = options.first { it == initialVal }

            return EnumSetting(
                name,
                description,
                displayIcon,
                options,
                { default },
                { initial },
            )
        }
    }
}

fun <E : Enum<E>> makeOption(enumValue: E): OptionSetting.Option<E> {
    if (enumValue !is IGameSettingOption) {
        throw IllegalArgumentException("Enum class must be an instance of IGameSettingOption")
    }

    return OptionSetting.Option(
        enumValue,
        enumValue.label,
        enumValue.description,
        enumValue.icon,
    )
}
