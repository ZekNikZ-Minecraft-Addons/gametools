package io.zkz.mc.gametools.event

import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class AbstractEvent : Event() {
    companion object {
        val handlerList = HandlerList()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }
}