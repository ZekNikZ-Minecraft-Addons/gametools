package io.zkz.mc.gametools.service

import io.zkz.mc.gametools.GTPlugin
import io.zkz.mc.gametools.data.IDataManager
import io.zkz.mc.gametools.data.IManagesData
import io.zkz.mc.gametools.injection.InjectionComponent
import org.bukkit.event.Listener

open class PluginService<T : GTPlugin<T>>(
    val plugin: T,
) : Listener, InjectionComponent {
    protected val logger get() = plugin.logger

    protected open fun setup() {}

    protected open fun onEnable() {}

    protected open fun onDisable() {}

    fun initialize() {
        // Initialization
        setup()

        // Load data
        if (this is IManagesData) {
            dataManagers.forEach(IDataManager::load)
        }

        // onEnable callback
        onEnable()

        // Link event handlers
        plugin.server.pluginManager.registerEvents(this, plugin)

        logger.info("Initialized service ${this::class.simpleName}")
    }

    fun cleanup() {
        // onDisable callback
        onDisable()

        // Save data
        if (this is IManagesData) {
            dataManagers.forEach {
                if (it.autoSave) {
                    it.save()
                }
            }
        }
    }
}
