package io.zkz.mc.gametools.team

import cloud.commandframework.bukkit.arguments.selector.MultiplePlayerSelector
import cloud.commandframework.bukkit.parsers.selector.MultiplePlayerSelectorArgument
import io.zkz.mc.gametools.command.CommandRegistry
import io.zkz.mc.gametools.command.CommandRegistryConnector
import io.zkz.mc.gametools.command.arguments.TeamArgument
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.team.TeamService.TeamCreationException
import io.zkz.mc.gametools.util.BukkitUtils.runNow
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.ComponentUtils.join
import io.zkz.mc.gametools.util.mm
import org.bukkit.command.CommandSender

@Injectable
object TeamCommands : CommandRegistry() {
    private val PERM_CREATE_DEFAULTS = permission("gametools.team.create.default", "Set up default teams")

//    private val PERM_CREATE = permission("gametools.team.create", "Create teams")
//    private val PERM_REMOVE = permission("gametools.team.remove", "Remove teams")
    private val PERM_JOIN = permission("gametools.team.join", "Add players to teams")
    private val PERM_LEAVE = permission("gametools.team.leave", "Remove players from teams")
    private val PERM_LIST = permission("gametools.team.list", "List registered teams")

    private val teamService by inject<TeamService>()

    override fun registerCommands(registry: CommandRegistryConnector) {
        val builder = registry.newBaseCommand("gteam")

        // Setup default teams
        registry.registerCommand(
            builder.literal("defaults")
                .permission(PERM_CREATE_DEFAULTS.name)
                .handler { cmd ->
                    runNow {
                        val sender: CommandSender = cmd.sender

                        try {
                            teamService.setupDefaultTeams()
                        } catch (exception: TeamCreationException) {
                            Chat.sendMessage(
                                sender,
                                ChatType.COMMAND_ERROR,
                                exception,
                                mm("could not set up default teams."),
                            )
                        }

                        Chat.sendMessage(sender, ChatType.COMMAND_SUCCESS, mm("Successfully set up default teams."))
                    }
                },
        )

        // Join team
        registry.registerCommand(
            builder.literal("join")
                .permission(PERM_JOIN.name)
                .argument(TeamArgument.of("team"))
                .argument(MultiplePlayerSelectorArgument.of("players"))
                .handler { cmd ->
                    runNow {
                        val sender: CommandSender = cmd.sender
                        val team: GameTeam = cmd.get("team")
                        val players: MultiplePlayerSelector = cmd.get("players")

                        players.players.forEach {
                            team.addMember(it.uniqueId)
                            Chat.sendMessage(
                                sender,
                                ChatType.COMMAND_SUCCESS,
                                mm("Added player '<0>' to team '<1>'.", mm(it.name), team.displayName),
                            )
                        }
                    }
                },
        )

        // Leave team
        registry.registerCommand(
            builder.literal("leave")
                .permission(PERM_LEAVE.name)
                .argument(MultiplePlayerSelectorArgument.of("players"))
                .handler { cmd ->
                    runNow {
                        val sender: CommandSender = cmd.sender
                        val players: MultiplePlayerSelector = cmd.get("players")

                        players.players.forEach {
                            teamService.leaveTeam(it.uniqueId)
                            Chat.sendMessage(
                                sender,
                                ChatType.COMMAND_SUCCESS,
                                mm("Removed player '<0>' from their team.", mm(it.name)),
                            )
                        }
                    }
                },
        )

        // List teams
        registry.registerCommand(
            builder.literal("list")
                .permission(PERM_LIST.name)
                .handler { cmd ->
                    runNow {
                        Chat.sendMessage(
                            cmd.sender,
                            teamService.allTeams
                                .map { team -> mm("<0> (<1>)", team.displayName, mm(team.id)) }
                                .join(mm(", ")),
                        )
                    }
                },
        )

        // Create team
//        val prefixFlag: CommandFlag<Component> = registry
//            .newFlag("prefix", TextComponentArgument.of("prefix"))
//            .build()
//        val colorFlag = registry
//            .newFlag("color", GTColorArgument.of("color"))
//            .build()
//        val formatTagFlag: CommandFlag<String> = registry
//            .newFlag("formatTag", StringArgument.of<CommandSender>("formatTag"))
//            .build()
//        val scoreboardColorFlag: CommandFlag<TextColor> = registry
//            .newFlag("scoreboardColor", TextColorArgument.of<CommandSender>("scoreboardColor"))
//            .build()
//
//        registry.registerCommand(
//            builder.literal("create", "new")
//                .permission(PERM_CREATE.name)
//                .argument(StringArgument.of("id"))
//                .argument(TextComponentArgument.of("name"))
//                .flag(prefixFlag)
//                .flag(colorFlag)
//                .flag(formatTagFlag)
//                .flag(scoreboardColorFlag)
//                .flag(registry.newFlag("spectator"))
//                .handler { cmd ->
//                    runNow {
//                        val teamBuilder: GameTeam.Builder = GameTeam.builder(cmd.get("id"), cmd.get("name"))
//                        cmd.flags().getValue(prefixFlag).ifPresent(teamBuilder::prefix)
//                        cmd.flags().getValue(colorFlag).ifPresent(teamBuilder::color)
//                        cmd.flags().getValue(formatTagFlag).ifPresent(teamBuilder::formatTag)
//                        cmd.flags().getValue(scoreboardColorFlag).ifPresent { scoreboardColor ->
//                            teamBuilder.scoreboardColor(
//                                NamedTextColor.nearestTo(
//                                    scoreboardColor,
//                                ),
//                            )
//                        }
//                        if (cmd.flags().isPresent("spectator")) {
//                            teamBuilder.spectator(true)
//                        }
//                        val team = GameTeam(
//                            id = cmd.get("id"),
//                            name = cmd.get("name"),
//                            prefix = cmd.flags().get(prefixFlag)!!,
//
//                        )
//
//                        try {
//                            TeamService.getInstance().createTeam(team)
//                            Chat.sendMessage(
//                                cmd.getSender(),
//                                ChatType.COMMAND_SUCCESS,
//                                mmArgs("Successfully created team with id '<0>'.", team.id()),
//                            )
//                        } catch (e: TeamCreationException) {
//                            Chat.sendMessage(
//                                cmd.getSender(),
//                                ChatType.COMMAND_ERROR,
//                                e,
//                                mmArgs("could not create team with id '<0>'.", team.id()),
//                            )
//                        }
//                    }
//                },
//        )

        // Remove team
//        registry.registerCommand(
//            builder.literal("remove", "delete")
//                .permission(PERM_REMOVE.name)
//                .argument(TeamArgument.of("team"))
//                .handler { cmd ->
//                    runNow {
//                        val team: GameTeam = cmd.get("team")
//                        TeamService.getInstance().removeTeam(team.id())
//                        Chat.sendMessage(
//                            cmd.getSender(),
//                            ChatType.COMMAND_SUCCESS,
//                            mmArgs("Successfully removed team with id '<0>'.", team.id()),
//                        )
//                    }
//                },
//        )
    }
}
