package io.zkz.mc.gametools.util

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

object GTMiniMessage : InjectionComponent {
    private val gtColors by inject<GTColors>()

    val MM = MiniMessage.builder()
        .tags(
            TagResolver.builder()
                .resolver(StandardTags.hoverEvent())
                .resolver(StandardTags.clickEvent())
                .resolver(StandardTags.keybind())
                .resolver(StandardTags.translatable())
                .resolver(StandardTags.insertion())
                .resolver(StandardTags.font())
                .resolver(StandardTags.decorations())
                .resolver(StandardTags.gradient())
                .resolver(StandardTags.rainbow())
                .resolver(StandardTags.reset())
                .resolver(StandardTags.newline())
                .resolver(StandardTags.transition())
                .resolver(StandardTags.selector())
                .resolver(gtColors)
                .build(),
        )
        .build()
}

fun mmResolve(s: String, vararg tagResolvers: TagResolver): Component {
    return GTMiniMessage.MM.deserialize(s, *tagResolvers)
}

fun mm(s: String): Component {
    return mmResolve(s)
}

fun mm(s: String, vararg args: Component): Component {
    return mmResolve(
        s,
        *args.mapIndexed { i, arg -> Placeholder.component("$i", arg) }.toTypedArray(),
    )
}

fun mm(s: String, vararg args: Any?): Component {
    return mmResolve(
        s,
        *args.mapIndexed { i, arg -> Placeholder.unparsed("$i", arg.toString()) }.toTypedArray(),
    )
}
