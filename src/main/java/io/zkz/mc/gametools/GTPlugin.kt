package io.zkz.mc.gametools

import cloud.commandframework.bukkit.BukkitCommandManager
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.extra.confirmation.CommandConfirmationManager
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler
import cloud.commandframework.minecraft.extras.MinecraftHelp
import cloud.commandframework.paper.PaperCommandManager
import io.zkz.mc.gametools.command.CommandRegistry
import io.zkz.mc.gametools.command.CommandRegistryConnector
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.InjectionKey
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.util.ChatType
import io.zkz.mc.gametools.util.findClassesAnnotatedWith
import io.zkz.mc.gametools.util.mm
import org.bukkit.command.CommandSender
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStream
import java.util.concurrent.TimeUnit
import java.util.function.Function

abstract class GTPlugin<T : GTPlugin<T>> : JavaPlugin(), InjectionComponent {
    lateinit var commandManager: BukkitCommandManager<CommandSender>
        private set
    lateinit var commandConfirmationManager: CommandConfirmationManager<CommandSender>
        private set

    override fun onEnable() {
        // Setup command handlers
        setupCommands()

        // Inject the plugin itself
        injectionContainer.register(
            InjectionKey(
                this::class,
                "",
            ),
        ) {
            @Suppress("UNCHECKED_CAST")
            this as T
        }

        // Find annotated injectables
        findInjectables()

        // Build injectables
        buildInjectables()

        // Register plugin dependent injectables
        buildPluginDependentInjectables(server.pluginManager)

        // Register and enable services
        val packageName = this::class.java.packageName
        logger.info("Initializing services...")
        @Suppress("UNCHECKED_CAST")
        val services = injectionContainer.query {
            subclassOf(PluginService::class)
            declaredInPackage(packageName)
        } as List<PluginService<*>>
        services.forEach(PluginService<*>::initialize)

        // Register commands
        val commandRegistry = CommandRegistryConnector(this)
        logger.info("Initializing commands... ")
        @Suppress("UNCHECKED_CAST")
        val commands = injectionContainer.query {
            subclassOf(CommandRegistry::class)
            declaredInPackage(packageName)
        } as List<CommandRegistry>
        commands.forEach {
            it.registerCommands(commandRegistry)
        }

        // Register permissions
        logger.info("Initializing permissions... ")
        val permissions = commands.flatMap { it.permissions }
        permissions.forEach {
            server.pluginManager.addPermission(it)
            logger.info("Registered permission node " + it.name)
        }

        logger.info("Enabled " + this.name)
    }

    private fun setupCommands() {
        // This is a function that will provide a command execution coordinator that parses and executes commands
        // asynchronously
        val executionCoordinatorFunction = AsynchronousCommandExecutionCoordinator.builder<CommandSender>().build()

        // This function maps the command sender type of our choice to the bukkit command sender.
        // However, in this example we use the Bukkit command sender, and so we just need to map it
        // to itself
        val mapperFunction = Function.identity<CommandSender>()
        try {
            commandManager = PaperCommandManager(
                // Owning plugin
                this,
                // Coordinator function
                executionCoordinatorFunction,
                // Command Sender -> C
                mapperFunction,
                // C -> Command Sender
                mapperFunction,
            )
        } catch (e: Exception) {
            logger.severe("Failed to initialize the command this.manager")
            server.pluginManager.disablePlugin(this)
            return
        }

        // Create the Minecraft help menu system
        @Suppress("UNUSED_VARIABLE")
        var minecraftHelp = MinecraftHelp(
            // Help Prefix
            "/example help",
            // Audience mapper
            { s -> s },
            // Manager
            commandManager,
        )

        // Register Brigadier mappings
        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier()
        }

        // Register asynchronous completions
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            (commandManager as PaperCommandManager<CommandSender>).registerAsynchronousCompletions()
        }

        // Create the confirmation this.manager. This allows us to require certain commands to be
        // confirmed before they can be executed
        commandConfirmationManager = CommandConfirmationManager(
            30L,
            TimeUnit.SECONDS,
            // Action when confirmation is required
            { context ->
                context.commandContext.sender.sendMessage(
                    mm("<alert_warning>Confirmation required. Confirm using <alert_info>/${context.command.components[0].argument.name} confirm</alert_info>."),
                )
            },
            // Action when no confirmation is pending
            { sender ->
                sender.sendMessage(
                    mm("<alert_warning>You don't have any pending commands."),
                )
            },
        )

        // Register the confirmation processor. This will enable confirmations for commands that require it
        commandConfirmationManager.registerConfirmationProcessor(commandManager)

        // Override the default exception handlers
        MinecraftExceptionHandler<CommandSender>().withInvalidSyntaxHandler().withInvalidSenderHandler().withNoPermissionHandler().withArgumentParsingHandler().withCommandExecutionHandler().withDecorator(ChatType.COMMAND_ERROR::format).apply(commandManager) { s -> s }
    }

    override fun onDisable() {
        // Cleanup services
        injectionContainer.getAllOfType<PluginService<T>>().forEach(PluginService<T>::cleanup)

        logger.info("Disabled $name")
    }

    private fun findInjectables() {
        val injectables = findClassesAnnotatedWith(classLoader, this::class.java.packageName, Injectable::class)
        injectables.forEach { clazz ->
            println("Found injectable class ${clazz.qualifiedName}")

            val injectionKey: String = (clazz.annotations.first { it is Injectable } as Injectable).key

            // Object
            if (clazz.objectInstance != null) {
                injectionContainer.register(InjectionKey(clazz, injectionKey)) { clazz.objectInstance!! }
                return@forEach
            }

            // Normal class
            injectionContainer.registerConstructorBuilder(InjectionKey(clazz, injectionKey))
        }
    }

    open fun buildInjectables() {}

    fun getResourceAsStream(name: String): InputStream? = classLoader.getResourceAsStream(name)

    open fun buildPluginDependentInjectables(pluginManager: PluginManager) {}
}
