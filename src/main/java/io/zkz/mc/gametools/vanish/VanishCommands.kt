package io.zkz.mc.gametools.vanish

import cloud.commandframework.arguments.standard.StringArgument
import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector
import cloud.commandframework.bukkit.parsers.selector.SinglePlayerSelectorArgument
import cloud.commandframework.context.CommandContext
import io.zkz.mc.gametools.command.CommandRegistry
import io.zkz.mc.gametools.command.CommandRegistryConnector
import io.zkz.mc.gametools.hud.ActionBarService
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.util.BukkitUtils.runNextTick
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.mm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Injectable
object VanishCommands : CommandRegistry() {
    private val PERM_VANISH_SELF = permission("gametools.vanish.self", "Vanish self")
    private val PERM_VANISH_OTHERS = permission("gametools.vanish.others", "Vanish other players")

    private val vanishingService by inject<VanishingService>()
    private val actionBarService by inject<ActionBarService>()

    override fun registerCommands(registry: CommandRegistryConnector) {
        val builder = registry.newBaseCommand("vanish", "v")

        // Base
        registry.registerCommand(
            builder
                .permission(PERM_VANISH_SELF.name)
                .handler {
                    val sender = it.sender
                    if (sender !is Player) {
                        Chat.sendMessage(
                            sender,
                            ChatType.COMMAND_ERROR,
                            mm("you cannot use this command from the console."),
                        )
                        return@handler
                    }

                    val reasons = vanishingService.getPlayerHiddenReasons(sender)
                    if (reasons != null && reasons.contains("manual")) {
                        // Already vanished
                        actionBarService.removeMessage(sender.uniqueId, "vanish")
                        runNextTick { vanishingService.showPlayer(sender, "manual") }
                    } else {
                        // Not already vanished
                        actionBarService.addMessage(sender.uniqueId, "vanish", mm("<alert_warning>You are currently vanished."))
                        runNextTick { vanishingService.hidePlayer(sender, "manual") }
                    }
                },
        )

        // Select reason
        registry.registerCommand(
            builder
                .permission(PERM_VANISH_SELF.name)
                .literal("self")
                .argument(
                    StringArgument.builder<CommandSender>("key")
                        .withSuggestionsProvider { cmd: CommandContext<CommandSender>, _: String ->
                            val sender = cmd.sender
                            if (sender !is Player) {
                                return@withSuggestionsProvider listOf<String>()
                            }
                            vanishingService.getPlayerHiddenReasons(sender.uniqueId)?.toList() ?: listOf()
                        }
                        .single()
                        .asRequired()
                        .build(),
                )
                .handler {
                    val sender = it.sender
                    if (sender !is Player) {
                        Chat.sendMessage(it.sender, ChatType.COMMAND_ERROR, mm("you cannot use this command from the console."))
                        return@handler
                    }
                    val key = it.get<String>("key")
                    val reasons = vanishingService.getPlayerHiddenReasons(sender)
                    if (reasons != null && reasons.contains(key)) {
                        // Already vanished
                        if (key == "manual") {
                            actionBarService.removeMessage(sender.uniqueId, "vanish")
                        }
                        runNextTick { vanishingService.showPlayer(sender, key) }
                    } else {
                        // Not already vanished
                        if (key == "manual") {
                            actionBarService.addMessage(sender.uniqueId, "vanish", mm("<alert_warning>You are currently vanished."))
                        }
                        runNextTick { vanishingService.hidePlayer(sender, key) }
                    }
                },
        )

        // Select reason and player
        registry.registerCommand(
            builder
                .permission(PERM_VANISH_OTHERS.name)
                .argument(SinglePlayerSelectorArgument.of("player"))
                .argument(
                    StringArgument.builder<CommandSender>("key")
                        .withSuggestionsProvider { cmd: CommandContext<CommandSender>, _: String ->
                            val sender = cmd.sender
                            if (sender !is Player) {
                                return@withSuggestionsProvider listOf<String>()
                            }
                            vanishingService.getPlayerHiddenReasons(sender.uniqueId)?.toList() ?: listOf()
                        }
                        .single()
                        .asRequired()
                        .build(),
                )
                .handler {
                    val p = it.get<SinglePlayerSelector>("player")
                    val player = p.getPlayer()
                    val key = it.get<String>("key")
                    if (player == null) {
                        Chat.sendMessage(it.sender, ChatType.COMMAND_ERROR, mm("you cannot use this command from the console."))
                        return@handler
                    }
                    val reasons = vanishingService.getPlayerHiddenReasons(player)
                    if (reasons != null && reasons.contains(key)) {
                        // Already vanished
                        if (key == "manual") {
                            actionBarService.removeMessage(player.uniqueId, "vanish")
                        }
                        runNextTick { vanishingService.showPlayer(player, key) }
                    } else {
                        // Not already vanished
                        if (key == "manual") {
                            actionBarService.addMessage(player.uniqueId, "vanish", mm("<alert_warning>You are currently vanished."))
                        }
                        runNextTick { vanishingService.hidePlayer(player, key) }
                    }
                },
        )
    }
}
