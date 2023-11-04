package io.zkz.mc.gametools.util

import io.zkz.mc.gametools.injection.Injectable
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.ParsingException
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags

@Injectable
class GTColors : TagResolver {
    @Throws(ParsingException::class)
    override fun resolve(name: String, arguments: ArgumentQueue, ctx: Context): Tag? {
        if (GTColor.COLORS.containsKey(name)) {
            return Tag.styling(GTColor.COLORS[name]!!.textColor)
        }

        return if (name.startsWith("legacy_")) {
            StandardTags.color().resolve(name.substring("legacy_".length), arguments, ctx)
        } else {
            StandardTags.color().resolve(name, arguments, ctx)
        }
    }

    override fun has(name: String): Boolean {
        return GTColor.COLORS.containsKey(name) || StandardTags.color()
            .has(name) || (name.startsWith("legacy_") && StandardTags.color().has(name.substring("legacy_".length)))
    }
}
