package io.zkz.mc.gametools.command

import cloud.commandframework.Command
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.flags.CommandFlag
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.meta.CommandMeta
import io.leangen.geantyref.TypeToken
import io.zkz.mc.gametools.GTPlugin
import org.bukkit.command.CommandSender
import java.util.function.Function

class CommandRegistryConnector(private val plugin: GTPlugin<*>) {
    fun newBaseCommand(command: String, vararg aliases: String): Command.Builder<CommandSender> {
        plugin.logger.info { "Registered command /$command" }
        return plugin.commandManager.commandBuilder(command, *aliases)
    }

    fun newConfirmableCommand(command: String): Command.Builder<CommandSender> {
        plugin.logger.info { "Registered confirmable command /$command" }
        val builder = plugin.commandManager.commandBuilder(command)
        plugin.commandManager.command(
            builder.literal("confirm")
                .meta(CommandMeta.DESCRIPTION, "Confirm a pending command")
                .handler(plugin.commandConfirmationManager.createConfirmationExecutionHandler())
        )
        return builder
    }

    fun registerCommand(commandBuilder: Command.Builder<CommandSender>) {
        plugin.commandManager.command(commandBuilder)
    }

    fun <T> registerArgument(
        clazz: Class<T>,
        supplier: Function<ParserParameters, ArgumentParser<CommandSender, *>>
    ) {
        plugin.commandManager.parserRegistry().registerParserSupplier(
            TypeToken.get(clazz),
            supplier
        )
    }

    fun newFlag(name: String): CommandFlag.Builder<Void> {
        return plugin.commandManager.flagBuilder(name)
    }

    fun <C, T> newFlag(name: String, argument: CommandArgument<C, T>): CommandFlag.Builder<T> {
        return plugin.commandManager.flagBuilder(name).withArgument(argument)
    }
}