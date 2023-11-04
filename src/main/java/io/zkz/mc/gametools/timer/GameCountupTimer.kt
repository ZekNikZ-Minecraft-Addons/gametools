package io.zkz.mc.gametools.timer

import org.bukkit.plugin.java.JavaPlugin

class GameCountupTimer(
    plugin: JavaPlugin,
    refreshRateTicks: Long,
) : AbstractTimer(
    plugin,
    refreshRateTicks,
) {
    private var startTime: Long = 0
    private var pausedCurrentTime: Long = -1

    override fun onStart() {
        startTime = System.currentTimeMillis()
    }

    override fun onUpdate() {
        // not needed
    }

    override fun onStop() {
        // not needed
    }

    override fun onPause() {
        pausedCurrentTime = currentTimeMillis
    }

    override fun onUnpause() {
        startTime = System.currentTimeMillis() - pausedCurrentTime
        pausedCurrentTime = -1
    }

    override val currentTimeMillis: Long
        get() = System.currentTimeMillis() - startTime

    override fun isReadyToRun(event: ScheduledEvent, currentTimeMillis: Long): Boolean {
        return currentTimeMillis >= event.delay
    }

    override fun isReadyToRun(event: ScheduledRepeatingEvent, lastRun: Long, currentTimeMillis: Long): Boolean {
        return currentTimeMillis >= event.delay && currentTimeMillis - lastRun >= event.period
    }
}
