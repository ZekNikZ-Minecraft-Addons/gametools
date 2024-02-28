package io.zkz.mc.gametools.event

import org.bukkit.event.Cancellable

abstract class AbstractCancellableEvent : AbstractEvent(), Cancellable {
    private var cancelled = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }
}
