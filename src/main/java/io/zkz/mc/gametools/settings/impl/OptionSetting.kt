package io.zkz.mc.gametools.settings.impl

import io.zkz.mc.gametools.settings.IGameSetting
import io.zkz.mc.gametools.settings.IGameSettingOption
import io.zkz.mc.gametools.util.ISB
import io.zkz.mc.gametools.util.observable.AbstractObservable
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

open class OptionSetting<T>(
    override val name: Component,
    override val description: Component?,
    override val displayIcon: ItemStack,
    private val options: List<Option<T>>,
    val defaultValue: () -> Option<T>,
    initialValue: () -> Option<T> = defaultValue,
) : AbstractObservable<IGameSetting<T>>(), IGameSetting<T> {
    @JvmRecord
    data class Option<T>(
        val value: T,
        override val name: Component,
        override val description: Component?,
        override val icon: ItemStack,
    ) : IGameSettingOption

    private var index: Int = options.indexOf(initialValue())

    override var value: T
        get() = options[index].value
        set(value) {
            setOption(
                options.firstOrNull { it.value == value }
                    ?: throw IndexOutOfBoundsException("Value is not an valid index"),
            )
        }

    fun setOption(value: Option<T>) {
        index = options.indexOf(value)
    }

    override fun resetToDefaultValue() = setOption(defaultValue())

    override val optionIcon: ItemStack
        get() {
            return ISB
                .fromStack(options[index].icon).run {
                    name(options[index].name)

                    if (options[index].description != null) {
                        lore(options[index].description!!)
                    }

                    addItemFlags(
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_UNBREAKABLE,
                        ItemFlag.HIDE_DESTROYS,
                        ItemFlag.HIDE_PLACED_ON,
                        ItemFlag.HIDE_ITEM_SPECIFICS,
                        ItemFlag.HIDE_DYE,
                    )

                    build()
                }
        }

    override fun handleLeftClick() {
        --index
        if (index < 0) {
            index = options.size - 1
        }
    }

    override fun handleRightClick() {
        ++index
        if (index >= options.size) {
            index = 0
        }
    }

    companion object {
        fun <T : IGameSettingOption> create(
            name: Component,
            description: Component?,
            displayIcon: ItemStack,
            options: () -> List<T>,
            defaultValue: () -> T,
            initialValue: () -> T = defaultValue,
        ): OptionSetting<T> {
            val actualOptions = options().map {
                Option(
                    it,
                    it.name,
                    it.description,
                    it.icon,
                )
            }

            return OptionSetting(
                name,
                description,
                displayIcon,
                actualOptions,
                {
                    actualOptions.firstOrNull { it.value == defaultValue() }
                        ?: throw IllegalStateException("Default option is not one of the available options")
                },
                {
                    actualOptions.firstOrNull { it.value == initialValue() }
                        ?: throw IllegalStateException("Initial option is not one of the available options")
                },
            )
        }
    }
}
