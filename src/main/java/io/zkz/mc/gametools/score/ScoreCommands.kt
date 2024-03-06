package io.zkz.mc.gametools.score

import cloud.commandframework.arguments.standard.DoubleArgument
import cloud.commandframework.arguments.standard.IntegerArgument
import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector
import cloud.commandframework.bukkit.parsers.selector.MultiplePlayerSelectorArgument
import io.zkz.mc.gametools.command.CommandRegistry
import io.zkz.mc.gametools.command.CommandRegistryConnector
import io.zkz.mc.gametools.command.arguments.TeamArgument
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.util.BukkitUtils.runNow
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.mm
import org.bukkit.command.CommandSender
import kotlin.jvm.optionals.getOrNull

@Injectable
object ScoreCommands : CommandRegistry() {
    private val PERM_ADD_POINTS = permission("gametools.score.add", "Add points to a player")
    private val PERM_SET_MULTIPLIER = permission("gametools.score.multiplier", "Set the default score multiplier")

    private val scoreService by inject<ScoreService>()

    override fun registerCommands(registry: CommandRegistryConnector) {
        val builder = registry.newBaseCommand("points")

        // Add points
        registry.registerCommand(
            builder.literal("add")
                .permission(PERM_ADD_POINTS.name)
                .argument(MultiplePlayerSelectorArgument.optional("players"))
                .argument(TeamArgument.of("team"))
                .argument(DoubleArgument.of("points"))
                .argument(IntegerArgument.optional("roundIndex"))
                .argument(StringArgument.optional("gameId"))
                .argument(StringArgument.optional("reason", StringArgument.StringMode.GREEDY))
                .handler { cmd ->
                    runNow {
                        val sender: CommandSender = cmd.sender
                        val players = cmd.get<MultiplePlayerSelector>("players").players
                        val team = cmd.getOptional<GameTeam>("team").getOrNull()
                        val points = cmd.get<Double>("points")
                        val roundIndex = cmd.getOptional<Int>("roundIndex").getOrNull()
                        val gameId = cmd.getOptional<String>("gameId").getOrNull()
                        val reason = cmd.getOptional<String>("reason").getOrNull() ?: "command"

                        scoreService.earnPoints(
                            players,
                            reason,
                            points,
                            team,
                            roundIndex,
                            gameId,
                        )

                        Chat.sendMessage(sender, ChatType.COMMAND_SUCCESS, mm("Successfully added points to player(s)"))
                    }
                },
        )

        // Set multiplier
        registry.registerCommand(
            builder.literal("setmultiplier")
                .permission(PERM_SET_MULTIPLIER.name)
                .argument(DoubleArgument.of("multiplier"))
                .handler { cmd ->
                    runNow {
                        val sender: CommandSender = cmd.sender
                        val multiplier = cmd.get<Double>("multiplier")

                        scoreService.currentScoreMultiplier = multiplier

                        Chat.sendMessage(
                            sender,
                            ChatType.COMMAND_SUCCESS,
                            mm("Successfully set multiplier to $multiplier"),
                        )
                    }
                },
        )
    }
}
