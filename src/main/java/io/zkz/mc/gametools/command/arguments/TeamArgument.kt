package io.zkz.mc.gametools.command.arguments

import cloud.commandframework.ArgumentDescription
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.arguments.standard.UUIDArgument.UUIDParser
import cloud.commandframework.captions.CaptionVariable
import cloud.commandframework.captions.StandardCaptionKeys
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import cloud.commandframework.exceptions.parsing.ParserException
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.teams.GameTeam
import io.zkz.mc.gametools.teams.TeamService
import java.io.Serial
import java.util.*
import java.util.function.BiFunction

class TeamArgument<C : Any> private constructor(
    required: Boolean,
    name: String,
    defaultValue: String,
    suggestionsProvider: BiFunction<CommandContext<C>, String, List<String>>?,
    defaultDescription: ArgumentDescription,
) : CommandArgument<C, GameTeam>(
    required,
    name,
    TeamParser<C>(),
    defaultValue,
    GameTeam::class.java,
    suggestionsProvider,
    defaultDescription,
),
    InjectionComponent {
    class Builder<C : Any>(name: String) : CommandArgument.Builder<C, GameTeam>(GameTeam::class.java, name) {
        /**
         * Builder a new example component
         *
         * @return Constructed component
         */
        override fun build(): CommandArgument<C, GameTeam> {
            return TeamArgument(
                this.isRequired,
                name,
                defaultValue,
                suggestionsProvider,
                defaultDescription,
            )
        }
    }

    class TeamParser<C : Any> : ArgumentParser<C, GameTeam>, InjectionComponent {
        private val teamService by inject<TeamService>()

        override fun parse(
            commandContext: CommandContext<C>,
            inputQueue: Queue<String>,
        ): ArgumentParseResult<GameTeam> {
            val input = inputQueue.peek()
                ?: return ArgumentParseResult.failure(
                    NoInputProvidedException(
                        TeamParser::class.java,
                        commandContext,
                    ),
                )
            val team: GameTeam = teamService.getTeam(input)
                ?: return ArgumentParseResult.failure(TeamParseException(input, commandContext))
            inputQueue.remove()
            return ArgumentParseResult.success(team)
        }

        override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
            return teamService.allTeams.map { it.id }
        }

        override fun isContextFree(): Boolean {
            return true
        }
    }

    class TeamParseException(
        private val input: String,
        context: CommandContext<*>,
    ) : ParserException(
        UUIDParser::class.java,
        context,
        StandardCaptionKeys.ARGUMENT_PARSE_FAILURE_UUID,
        CaptionVariable.of("input", input),
    ) {
        companion object {
            @Serial
            private val serialVersionUID = 6399602590976540023L
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is TeamParseException) return false

            if (input != other.input) return false

            return true
        }

        override fun hashCode(): Int {
            return input.hashCode()
        }
    }

    companion object {
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
        fun <C : Any> of(name: String): CommandArgument<C, GameTeam> {
            return builder<C>(name).asRequired().build()
        }

        /**
         * Create a new optional command component
         *
         * @param name Component name
         * @param <C>  Command sender type
         * @return Created component
         </C> */
        fun <C : Any> optional(name: String): CommandArgument<C, GameTeam> {
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
        ): CommandArgument<C, GameTeam> {
            return builder<C>(name).asOptionalWithDefault(defaultUUID.toString()).build()
        }
    }
}
