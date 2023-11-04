package io.zkz.mc.gametools.service

import io.zkz.mc.gametools.GTPlugin
import io.zkz.mc.gametools.injection.InjectionComponent
import org.bukkit.event.Listener

open class PluginService<T : GTPlugin<T>>(
    val plugin: T,
) : Listener, InjectionComponent {
    private val logger get() = plugin.logger

    protected open fun setup() {}

    protected open fun onEnable() {}

    protected open fun onDisable() {}

    fun initialize() {
        // Initialization
        setup()

        // onEnable callback
        onEnable()

        // Link event handlers
        plugin.server.pluginManager.registerEvents(this, plugin)

        logger.info("Initialized service ${this::class.simpleName}")
    }

    fun cleanup() {
        // onDisable callback
        onDisable()
    }
}
