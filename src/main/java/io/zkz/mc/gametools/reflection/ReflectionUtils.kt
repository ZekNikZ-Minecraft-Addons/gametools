package io.zkz.mc.gametools.reflection

import io.zkz.mc.gametools.GTPlugin
import io.zkz.mc.gametools.command.CommandRegistryConnector
import org.bukkit.permissions.Permission
import org.reflections.ReflectionUtils
import org.reflections.Reflections
import org.reflections.scanners.Scanners.MethodsAnnotated
import org.reflections.scanners.Scanners.TypesAnnotated
import org.reflections.util.ConfigurationBuilder
import org.reflections.util.ReflectionUtilsPredicates
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.function.Consumer
import java.util.logging.Level

fun findAndRegisterCommands(loader: ClassLoader, plugin: GTPlugin<*>, registry: CommandRegistryConnector) {
    val packageName = plugin.javaClass.packageName

    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackage(packageName, loader)
            .addScanners(MethodsAnnotated)
    )

    reflections.get(
        MethodsAnnotated.with(RegisterCommands::class.java)
            .`as`(Method::class.java)
            .filter(
                ReflectionUtilsPredicates.withReturnType(Void.TYPE)
                    .and(ReflectionUtilsPredicates.withParameters(CommandRegistryConnector::class.java))
                    .and(ReflectionUtilsPredicates.withStatic())
            )
    ).forEach(Consumer forEach@{ method: Method ->
        if (!method.declaringClass.packageName.startsWith(packageName)) {
            return@forEach
        }

        try {
            method.isAccessible = true
            method.invoke(null, registry)
        } catch (e: IllegalAccessException) {
            plugin.logger.log(
                Level.WARNING,
                "Could not register commands using method ${method.name} in ${method.declaringClass.canonicalName}",
                e
            )
        } catch (e: InvocationTargetException) {
            plugin.logger.log(
                Level.WARNING,
                "Could not register commands using method ${method.name} in ${method.declaringClass.canonicalName}",
                e
            )
        }
    })
}

fun findPermissions(loader: ClassLoader?, plugin: GTPlugin<*>): List<Permission> {
    val packageName = plugin.javaClass.packageName

    val reflections = Reflections(
        ConfigurationBuilder()
            .forPackage(packageName, loader)
            .addScanners(TypesAnnotated)
    )

    val res: MutableList<Permission> = ArrayList()
    reflections.get(
        TypesAnnotated.with(RegisterPermissions::class.java)
            .asClass<Any>()
    ).forEach(Consumer { clazz: Class<*>? ->
        reflections.get(
            ReflectionUtils.Fields.of(clazz)
                .`as`(Field::class.java)
                .filter(
                    ReflectionUtilsPredicates.withType(Permission::class.java)
                        .and(ReflectionUtilsPredicates.withStatic())
                )
        ).forEach(Consumer forEach@{ field: Field ->
            if (!field.declaringClass.packageName.startsWith(packageName)) {
                return@forEach
            }

            if (!Modifier.isFinal(field.modifiers)) {
                plugin.logger.warning(
                    "Field ${field.name} in ${field.declaringClass.canonicalName} will be registered as a permission node but is not final."
                )
            }

            try {
                field.isAccessible = true
                res.add(field[null] as Permission)
            } catch (e: IllegalAccessException) {
                plugin.logger.log(
                    Level.WARNING,
                    "Could not register permission in field ${field.name} in ${field.declaringClass.canonicalName}",
                    e
                )
            }
        })
    })

    return res
}