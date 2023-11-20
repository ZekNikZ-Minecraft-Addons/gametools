package io.zkz.mc.gametools.util

import com.mojang.brigadier.context.CommandContext
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ComponentArgument

object ComponentUtils {
    fun Component.join(args: Iterable<Component>): Component {
        val res = Component.text()
        var first = true
        for (comp in args) {
            if (!first) {
                res.append(this)
            }
            res.append(comp)
            first = false
        }
        return res.build()
    }

    fun Component.join(vararg args: Component): Component = join(listOf(*args))

    fun List<Component>.join(joiner: Component): Component {
        return joiner.join(this)
    }

    fun extractArgument(cmd: CommandContext<CommandSourceStack>, name: String): Component {
        return GsonComponentSerializer.gson().deserialize(net.minecraft.network.chat.Component.Serializer.toJson(ComponentArgument.getComponent(cmd, name)))
    }

    fun comparator(): Comparator<Component> {
        return Comparator.comparing(PlainTextComponentSerializer.plainText()::serialize)
    }

    fun <T> comparing(keyExtractor: (T) -> Component): Comparator<T> {
        return Comparator.comparing { PlainTextComponentSerializer.plainText().serialize(keyExtractor(it)) }
    }

    private class ComponentJoiner(private val joiner: Component) {
        private val els: MutableList<Component> = mutableListOf()

        fun add(component: Component) {
            els.add(component)
        }

        fun merge(other: ComponentJoiner): ComponentJoiner {
            els.addAll(other.els)
            return this
        }

        fun build(): Component {
            return joiner.join(els)
        }
    }
}
