package io.zkz.mc.gametools.util

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

object Chat {
    fun sendMessage(audience: Audience, points: Double, message: Component?) {
        audience.sendMessage(ChatType.NORMAL.format(message!!, points))
    }

    fun sendMessage(audience: Audience, message: Component?) {
        audience.sendMessage(ChatType.NORMAL.format(message!!))
    }

    fun sendMessage(points: Double, message: Component?) {
        sendMessage(Audience.audience(Bukkit.getOnlinePlayers()), ChatType.NORMAL, points, message)
    }

    fun sendMessage(message: Component?) {
        sendMessage(Audience.audience(Bukkit.getOnlinePlayers()), ChatType.NORMAL, message)
    }

    fun sendMessage(audience: Audience, type: ChatType, points: Double, message: Component?) {
        audience.sendMessage(type.format(message!!, points))
    }

    fun sendMessage(audience: Audience, type: ChatType, cause: Throwable?, message: Component?) {
        audience.sendMessage(type.format(message!!, cause!!))
    }

    fun sendMessage(audience: Audience, type: ChatType, message: Component?) {
        audience.sendMessage(type.format(message!!))
    }

    fun sendMessage(type: ChatType, points: Double, message: Component?) {
        sendMessage(Audience.audience(Bukkit.getOnlinePlayers()), type, points, message)
    }

    fun sendMessage(type: ChatType, message: Component?) {
        sendMessage(Audience.audience(Bukkit.getOnlinePlayers()), type, message)
    }
}
