package io.zkz.mc.gametools.readyup

import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.extra.confirmation.CommandConfirmationManager
import io.zkz.mc.gametools.command.CommandRegistry
import io.zkz.mc.gametools.command.CommandRegistryConnector
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.util.BukkitUtils.runNow
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.mm
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault

@Injectable
object ReadyUpCommands : CommandRegistry() {
    private val PERM_READY_BASE = permission("gametools.ready", "Ready up", PermissionDefault.TRUE)
    private val PERM_READY_STATUS = permission("gametools.ready.status", "See the ready status of the game")
    private val PERM_READY_UNDO = permission("gametools.ready.undo", "Undo the ready up of a player")

    private val readyUpService by inject<ReadyUpService>()

    override fun registerCommands(registry: CommandRegistryConnector) {
        val builder = registry.newConfirmableCommand("ready")

        // Ready up
        registry.registerCommand(
            builder
                .permission(PERM_READY_BASE.name)
                .handler {
                    runNow {
                        val sender = it.sender
                        if (sender !is Player) {
                            Chat.sendMessage(
                                sender,
                                ChatType.COMMAND_ERROR,
                                mm("you cannot use this command from the console.")
                            )
                            return@runNow
                        }
                        if (!readyUpService.recordReady(sender)) {
                            Chat.sendMessage(sender, ChatType.COMMAND_ERROR, mm("nothing is waiting for you to be ready."))
                        } else {
                            Chat.sendMessage(sender, ChatType.COMMAND_SUCCESS, mm("You are now ready!"))
                        }
                    }
                }
        )

        // Status
        registry.registerCommand(
            builder.literal("status")
                .permission(PERM_READY_STATUS.name)
                .handler { runNow { readyUpService.sendStatus(it.sender) } }
        )

        // Undo
        registry.registerCommand(
            builder.literal("undo")
                .permission(PERM_READY_UNDO.name)
                .argument(
                    StringArgument.builder<CommandSender>("player")
                        .single()
                        .withSuggestionsProvider { _, _ -> readyUpService.allReadyPlayerNames.toList() }
                        .asRequired()
                        .build()
                )
                .handler {
                    runNow {
                        val sender = it.sender
                        val playerName: String = it.get("player")
                        val player = Bukkit.getOfflinePlayer(playerName)
                        if (!readyUpService.undoReady(player.uniqueId)) {
                            Chat.sendMessage(sender, ChatType.COMMAND_ERROR, mm("that player was not marked as ready."))
                        } else {
                            Chat.sendMessage(sender, ChatType.COMMAND_SUCCESS, mm("Marked player <0> as not ready.", playerName))
                        }
                    }
                }
        )

        // Bypass
        registry.registerCommand(
            builder.literal("bypass")
                .meta(CommandConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .handler {
                    runNow {
                        val sessions: Map<Int, ReadyUpSession> = readyUpService.getSessions()
                        sessions.values.forEach(ReadyUpSession::complete)
                    }
                }
        )

        // Test
        // TODO: remove
        registry.registerCommand(
            builder.literal("test")
                .handler {
                    runNow {
                        readyUpService.waitForReady(
                            Bukkit.getOnlinePlayers().map { it.uniqueId },
                            { Bukkit.getServer().sendMessage(mm("<aqua>Done waiting for ready!")) }
                        )
                    }
                }
        )
    }
}
