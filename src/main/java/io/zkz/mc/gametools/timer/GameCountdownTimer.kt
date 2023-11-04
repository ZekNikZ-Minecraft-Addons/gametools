package io.zkz.mc.gametools.timer

import org.bukkit.plugin.java.JavaPlugin
import kotlin.time.Duration.Companion.convert
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class GameCountdownTimer constructor(
    plugin: JavaPlugin,
    refreshRateTicks: Long,
    timerValue: Long,
    timerValueUnits: DurationUnit,
    private val onDone: Runnable? = null
) : AbstractTimer(
    plugin,
    refreshRateTicks
) {
    private val timerValueMillis: Long
    private var startTime: Long = 0
    private var pausedTimeRemaining: Long = -1

    init {
        timerValueMillis = convert(timerValue.toDouble(), timerValueUnits, DurationUnit.MILLISECONDS).toLong()
    }

    override fun onStart() {
        startTime = System.currentTimeMillis()
        onUpdate()
    }

    override fun onUpdate() {
        if (isDone) {
            return
        }
        if (currentTimeMillis <= 0) {
            onDone?.run()
            stop()
        }
    }

    override fun onStop() {
        // not needed
    }

    // total time - time elapsed
    override val currentTimeMillis: Long
        get() = timerValueMillis - (System.currentTimeMillis() - startTime)

    override fun onPause() {
        pausedTimeRemaining = currentTimeMillis
    }

    override fun onUnpause() {
        startTime = pausedTimeRemaining - timerValueMillis + System.currentTimeMillis()
        pausedTimeRemaining = -1
    }

    override fun isReadyToRun(event: ScheduledEvent, currentTimeMillis: Long): Boolean {
        return currentTimeMillis <= event.delay
    }

    override fun isReadyToRun(event: ScheduledRepeatingEvent, lastRun: Long, currentTimeMillis: Long): Boolean {
        // Simple way to avoid adding another condition below
        var lastRunMillis = lastRun
        if (lastRun == -1L) {
            lastRunMillis = Long.MAX_VALUE
        }
        return timerValueMillis - currentTimeMillis >= event.delay && lastRunMillis - currentTimeMillis >= event.period
    }
}
