package io.zkz.mc.gametools.commands

import io.zkz.mc.gametools.command.CommandRegistry
import io.zkz.mc.gametools.command.CommandRegistryConnector
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.mm
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

@Injectable
class VanishCommands2 : CommandRegistry() {
    private val PERM_VANISH_SELF = register(Permission("gametools.vanish.self", "Vanish self"))
    private val PERM_VANISH_OTHERS = register(Permission("gametools.vanish.others", "Vanish other players"))

    override fun registerCommands(registry: CommandRegistryConnector) {
        val builder = registry.newBaseCommand("vanish", "v")

        registry.registerCommand(
            builder
                .permission(PERM_VANISH_SELF.name)
                .handler {
                    val sender = it.sender
                    if (sender !is Player) {
                        Chat.sendMessage(
                            sender,
                            ChatType.COMMAND_ERROR,
                            mm("you cannot use this command from the console.")
                        )
                        return@handler
                    }


                }
        )
    }
}