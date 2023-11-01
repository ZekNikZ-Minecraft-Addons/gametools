package io.zkz.mc.gametools.command

import io.zkz.mc.gametools.injection.InjectionComponent
import org.bukkit.permissions.Permission

open class CommandRegistry : InjectionComponent {
    private val _permissions: MutableList<Permission> = mutableListOf()

    val permissions: List<Permission>
        get() = _permissions.toList()

    open fun registerCommands(registry: CommandRegistryConnector) = Unit

    protected fun register(permission: Permission): Permission {
        _permissions.add(permission)
        return permission
    }
}