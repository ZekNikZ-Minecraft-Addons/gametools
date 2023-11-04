package io.zkz.mc.gametools.command

import io.zkz.mc.gametools.injection.InjectionComponent
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionDefault

open class CommandRegistry : InjectionComponent {
    private val _permissions: MutableList<Permission> = mutableListOf()

    val permissions: List<Permission>
        get() = _permissions.toList()

    open fun registerCommands(registry: CommandRegistryConnector) = Unit

    protected fun permission(name: String, description: String, defaultValue: PermissionDefault = PermissionDefault.OP): Permission {
        val permission = Permission(name, description, defaultValue)
        _permissions.add(permission)
        return permission
    }
}