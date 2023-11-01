package io.zkz.mc.gametools.command.arguments

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.captions.CaptionVariable
import cloud.commandframework.captions.StandardCaptionKeys
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import cloud.commandframework.exceptions.parsing.ParserException
import com.mojang.brigadier.StringReader
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.commands.arguments.ComponentArgument.ERROR_INVALID_JSON
import java.io.Serial
import java.util.*
import java.util.function.BiFunction

class TextComponentArgument<C : Any> private constructor(
    required: Boolean,
    name: String,
    defaultValue: String,
    suggestionsProvider: BiFunction<CommandContext<C>, String, List<String>>,
    defaultDescription: ArgumentDescription,
) : CommandArgument<C, Component>(
    required,
    name,
    TextComponentParser<C>(),
    defaultValue,
    Component::class.java,
    suggestionsProvider,
    defaultDescription
) {
    class Builder<C : Any>(name: String) : CommandArgument.Builder<C, Component>(
        Component::class.java, name
    ) {
        override fun build(): CommandArgument<C, Component> {
            return TextComponentArgument<C>(
                isRequired,
                name,
                defaultValue,
                suggestionsProvider,
                defaultDescription
            )
        }
    }

    class TextComponentParser<C : Any> : ArgumentParser<C, Component> {
        override fun parse(
            commandContext: CommandContext<C>,
            inputQueue: Queue<String>,
        ): ArgumentParseResult<Component> {
            val peek = inputQueue.peek()
                ?: return ArgumentParseResult.failure(
                    NoInputProvidedException(
                        TextComponentParser::class.java,
                        commandContext
                    )
                )
            var i = 0
            val stringJoiner = StringJoiner(" ")
            lateinit var lastError: Throwable
            while (i < inputQueue.size) {
                stringJoiner.add((inputQueue as LinkedList<String>)[i])
                ++i
                val stringReader = StringReader(stringJoiner.toString())
                lastError = try {
                    val component: net.minecraft.network.chat.Component? =
                        net.minecraft.network.chat.Component.Serializer.fromJson(stringReader)
                    return if (component == null) {
                        throw ERROR_INVALID_JSON.createWithContext(stringReader, "empty")
                    } else {
                        for (j in 1 until i) {
                            inputQueue.remove()
                        }
                        ArgumentParseResult.success<Component>(
                            GsonComponentSerializer.gson()
                                .deserialize(net.minecraft.network.chat.Component.Serializer.toJson(component))
                        )
                    }
                } catch (var4: Exception) {
                    val string = if (var4.cause != null) var4.cause!!.message else var4.message
                    //                    throw ERROR_INVALID_JSON.createWithContext(stringReader, string);
                    val e: Exception = JsonParseException(
                        string!!, commandContext
                    )
                    e.printStackTrace()
                    e
                }
            }
            return ArgumentParseResult.failure(lastError)
        }

        override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
            return emptyList()
        }

        override fun isContextFree(): Boolean {
            return true
        }
    }

    class JsonParseException(
        input: String,
        context: CommandContext<*>,
    ) : ParserException(
        StringArgument.StringParser::class.java,
        context,
        StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_STRING,
        CaptionVariable.of("input", input)
    ) {
        companion object {
            @Serial
            private val serialVersionUID = -8903115465005472945L
        }
    }

    companion object {
        /**
         * Create a new builder
         *
         * @param name Name of the argument
         * @param <C>  Command sender type
         * @return Created builder
        </C> */
        fun <C : Any> newBuilder(name: String): CommandArgument.Builder<C, Component> {
            return Builder(name)
        }

        /**
         * Create a new required argument
         *
         * @param name Argument name
         * @param <C>  Command sender type
         * @return Created argument
        </C> */
        fun <C : Any> of(name: String): CommandArgument<C, Component> {
            return newBuilder<C>(name).asRequired().build()
        }

        /**
         * Create a new optional argument
         *
         * @param name Argument name
         * @param <C>  Command sender type
         * @return Created argument
        </C> */
        fun <C : Any> optional(name: String): CommandArgument<C, Component> {
            return newBuilder<C>(name).asOptional().build()
        }

        /**
         * Create a new optional argument with a default value
         *
         * @param name         Argument name
         * @param defaultValue Default value
         * @param <C>          Command sender type
         * @return Created argument
        </C> */
        fun <C : Any> optional(
            name: String,
            defaultValue: String,
        ): CommandArgument<C, Component> {
            return newBuilder<C>(name).asOptionalWithDefault(defaultValue).build()
        }
    }
}