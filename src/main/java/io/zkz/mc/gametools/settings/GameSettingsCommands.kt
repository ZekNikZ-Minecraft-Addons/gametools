package io.zkz.mc.gametools.settings

import io.zkz.mc.gametools.command.CommandRegistry
import io.zkz.mc.gametools.command.CommandRegistryConnector
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.util.BukkitUtils.runNow
import io.zkz.mc.gametools.util.Chat
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.mm
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@Injectable
object GameSettingsCommands : CommandRegistry() {
    val PERM_CHANGE_SETTINGS = permission("gametools.settings.change", "Change game settings")

    private val gameSettingsService by inject<GameSettingsService>()

    override fun registerCommands(registry: CommandRegistryConnector) {
        val builder = registry.newBaseCommand("settings")

        registry.registerCommand(
            builder
                .handler { cmd ->
                    val sender: CommandSender = cmd.sender

                    if (sender !is Player) {
                        Chat.sendMessage(sender, ChatType.COMMAND_ERROR, mm("you cannot use this command from the console."))
                        return@handler
                    }

                    runNow { gameSettingsService.openMenu(sender) }
                },
        )
    }
}
