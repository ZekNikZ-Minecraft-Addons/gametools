package io.zkz.mc.gametools.event

import org.bukkit.Bukkit
import org.bukkit.event.Event

fun event(event: Event) = Bukkit.getServer().pluginManager.callEvent(event)