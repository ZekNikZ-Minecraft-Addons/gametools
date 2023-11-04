package io.zkz.mc.gametools.command.arguments

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.captions.CaptionVariable
import cloud.commandframework.captions.StandardCaptionKeys
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import cloud.commandframework.exceptions.parsing.ParserException
import cloud.commandframework.minecraft.extras.TextColorArgument.TextColorParser
import io.leangen.geantyref.TypeToken
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.util.GTColor
import java.io.Serial
import java.util.*
import java.util.function.BiFunction
import java.util.regex.Pattern

class GTColorArgument<C : Any> private constructor(
    required: Boolean,
    name: String,
    defaultValue: String,
    suggestionsProvider: BiFunction<CommandContext<C>, String, List<String>>,
    defaultDescription: ArgumentDescription,
) : CommandArgument<C, GTColor>(
    required,
    name,
    GTColorParser<C>(),
    defaultValue,
    TypeToken.get(GTColor::class.java),
    suggestionsProvider,
    defaultDescription,
),
    InjectionComponent {
    class Builder<C : Any>(name: String) : CommandArgument.Builder<C, GTColor>(GTColor::class.java, name) {
        /**
         * Builder a new example component
         *
         * @return Constructed component
         */
        override fun build(): CommandArgument<C, GTColor> {
            return GTColorArgument(
                this.isRequired,
                name,
                defaultValue,
                suggestionsProvider,
                defaultDescription,
            )
        }
    }

    class GTColorParser<C : Any> : ArgumentParser<C, GTColor> {
        override fun parse(
            commandContext: CommandContext<C>,
            inputQueue: Queue<String>,
        ): ArgumentParseResult<GTColor> {
            val input = inputQueue.peek()
                ?: return ArgumentParseResult.failure(
                    NoInputProvidedException(
                        TextColorParser::class.java,
                        commandContext,
                    ),
                )
            for ((key, value) in GTColor.COLORS) {
                if (key.equals(input, ignoreCase = true)) {
                    inputQueue.remove()
                    return ArgumentParseResult.success(
                        value,
                    )
                }
            }
            if (HEX_PREDICATE.matcher(input).matches()) {
                inputQueue.remove()
                return ArgumentParseResult.success(
                    GTColor((if (input.startsWith("#")) input.substring(1) else input).toInt(16)),
                )
            }
            return ArgumentParseResult.failure(
                TextColorParseException(
                    commandContext,
                    input,
                ),
            )
        }

        override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
            val suggestions: MutableList<String> = LinkedList()
            if (input.isEmpty() || input == "#" || (
                HEX_PREDICATE.matcher(input).matches() &&
                    input.length < if (input.startsWith("#")) 7 else 6
                )
            ) {
                run {
                    var c = 'a'
                    while (c <= 'f') {
                        suggestions.add(String.format("%s%c", input, c))
                        suggestions.add(String.format("&%c", c))
                        c++
                    }
                }
                var c = '0'
                while (c <= '9') {
                    suggestions.add(String.format("%s%c", input, c))
                    suggestions.add(String.format("&%c", c))
                    c++
                }
            }
            suggestions.addAll(GTColor.COLORS.keys)
            return suggestions
        }
    }

    private class TextColorParseException(
        commandContext: CommandContext<*>,
        input: String,
    ) : ParserException(
        TextColorParser::class.java,
        commandContext,
        StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_COLOR,
        CaptionVariable.of("input", input),
    ) {
        companion object {
            @Serial
            private val serialVersionUID = -6236625328843879518L
        }
    }

    companion object {
        private val HEX_PREDICATE = Pattern.compile("#?([a-fA-F0-9]{1,6})")

        /**
         * Create a new builder
         *
         * @param name Name of the component
         * @param <C>  Command sender type
         * @return Created builder
         </C> */
        fun <C : Any> builder(name: String): Builder<C> {
            return Builder(name)
        }

        /**
         * Create a new required command component
         *
         * @param name Component name
         * @param <C>  Command sender type
         * @return Created component
         </C> */
        fun <C : Any> of(name: String): CommandArgument<C, GTColor> {
            return builder<C>(name).asRequired().build()
        }

        /**
         * Create a new optional command component
         *
         * @param name Component name
         * @param <C>  Command sender type
         * @return Created component
         </C> */
        fun <C : Any> optional(name: String): CommandArgument<C, GTColor> {
            return builder<C>(name).asOptional().build()
        }

        /**
         * Create a new required command component with a default value
         *
         * @param name        Component name
         * @param defaultUUID Default uuid
         * @param <C>         Command sender type
         * @return Created component
         </C> */
        fun <C : Any> optional(
            name: String,
            defaultUUID: UUID,
        ): CommandArgument<C, GTColor> {
            return builder<C>(name).asOptionalWithDefault(defaultUUID.toString()).build()
        }
    }
}
