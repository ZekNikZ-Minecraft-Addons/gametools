package io.zkz.mc.gametools.timer

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.convert
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

data class ScheduledEvent(
    val delay: Long,
    val hook: (currentTime: Long) -> Unit,
)

data class ScheduledRepeatingEvent(
    val delay: Long,
    val period: Long,
    val hook: (currentTime: Long, cancel: () -> Unit) -> Unit,
)

@OptIn(ExperimentalTime::class)
abstract class AbstractTimer protected constructor(
    private val plugin: JavaPlugin,
    private val refreshRate: Long,
) {
    private val hooks: MutableMap<Int, Runnable> = ConcurrentHashMap()
    private val tempHooks: MutableMap<Int, (cancel: () -> Unit) -> Unit> = ConcurrentHashMap()

    private var taskId = -1

    var isStarted = false
        private set

    var isDone = false
        private set

    private var nextHookId = 0

    private val events: MutableList<ScheduledEvent> = ArrayList()
    private val eventsCompleted: MutableList<Boolean> = ArrayList()

    private val repeatingEvents: MutableList<ScheduledRepeatingEvent> = ArrayList()
    private val repeatingEventsLastRunTimes: MutableList<Long> = ArrayList()
    private val repeatingEventsCancelled: MutableList<Boolean> = ArrayList()

    private fun update() {
        onUpdate()

        hooks.values.forEach(Runnable::run)
        tempHooks.forEach { (id, hook) -> hook { removeHook(id) } }

        // Run events
        val currentTime = currentTimeMillis
        for (i in events.indices) {
            val event = events[i]
            val completed = eventsCompleted[i]
            if (completed) {
                continue
            }
            if (isReadyToRun(event, currentTime)) {
                event.hook(currentTime)
                eventsCompleted[i] = true
            }
        }

        // Run repeating events
        for (i in repeatingEvents.indices) {
            val event = repeatingEvents[i]
            val lastRun = repeatingEventsLastRunTimes[i]
            val cancelled = repeatingEventsCancelled[i]
            if (cancelled) {
                continue
            }
            if (isReadyToRun(event, lastRun, currentTime)) {
                event.hook(currentTime) { repeatingEventsCancelled[i] = true }
                repeatingEventsLastRunTimes[i] = currentTime
            }
        }
    }

    protected abstract fun onStart()

    protected abstract fun onUpdate()

    protected abstract fun onPause()

    protected abstract fun onUnpause()

    protected abstract fun onStop()

    protected abstract fun isReadyToRun(
        event: ScheduledEvent,
        currentTimeMillis: Long,
    ): Boolean

    protected abstract fun isReadyToRun(
        event: ScheduledRepeatingEvent,
        lastRun: Long,
        currentTimeMillis: Long,
    ): Boolean

    fun start(): AbstractTimer {
        if (taskId == -1) {
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                ::update,
                refreshRate,
                refreshRate,
            )
            isStarted = true
            isDone = false
            onStart()
        }

        return this
    }

    fun pause() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId)
            taskId = -1
            onPause()
        }
    }

    fun unpause() {
        if (taskId == -1) {
            taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                plugin,
                { update() },
                refreshRate,
                refreshRate,
            )
            onUnpause()
        }
    }

    fun stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId)
            taskId = -1
            isStarted = false
            isDone = true
            onStop()
        }
    }

    val isRunning: Boolean
        get() = taskId != -1
    val isPaused: Boolean
        get() = isStarted && !isRunning

    fun addHook(hook: Runnable): Int {
        hooks[++nextHookId] = hook
        return nextHookId
    }

    fun addTempHook(hook: (cancel: () -> Unit) -> Unit): Int {
        tempHooks[++nextHookId] = hook
        return nextHookId
    }

    fun removeHook(hookId: Int) {
        hooks.remove(hookId)
        tempHooks.remove(hookId)
    }

    fun scheduleEvent(delay: Long, hook: (currentTime: Long) -> Unit) {
        events.add(ScheduledEvent(delay, hook))
        eventsCompleted.add(false)
    }

    fun scheduleRepeatingEvent(delay: Long, period: Long, hook: () -> Unit) {
        repeatingEvents.add(
            ScheduledRepeatingEvent(delay, period) { _, _ -> hook() },
        )
        repeatingEventsLastRunTimes.add(-1L)
        repeatingEventsCancelled.add(false)
    }

    fun scheduleRepeatingEvent(delay: Long, period: Long, hook: (currentTime: Long, cancel: () -> Unit) -> Unit) {
        repeatingEvents.add(ScheduledRepeatingEvent(delay, period, hook))
        repeatingEventsLastRunTimes.add(-1L)
        repeatingEventsCancelled.add(false)
    }

    protected abstract val currentTimeMillis: Long

    fun getCurrentTime(unit: DurationUnit): Long {
        return convert(currentTimeMillis.toDouble(), DurationUnit.MILLISECONDS, unit).toLong()
    }

    fun getModuloCurrentTime(unit: DurationUnit): Long {
        return getCurrentTime(unit) % when (unit) {
            DurationUnit.MINUTES, DurationUnit.SECONDS -> 60
            DurationUnit.MILLISECONDS -> 1000
            else -> 1000000
        }
    }

    /**
     * Format:
     * H, h     hour-of-day (0-23)          number            0
     * M, m     minute-of-hour              number            30
     * S, s     second-of-minute            number            55
     * L, l     millisecond-of-second       fraction          978
     *
     * @param format the format string
     * @return the formatted string
     */
    fun format(format: String): String {
        return format.replace("%H", "%02d".format(getModuloCurrentTime(DurationUnit.HOURS)))
            .replace("%h", "${getModuloCurrentTime(DurationUnit.HOURS)}")
            .replace("%M", "%02d".format(getModuloCurrentTime(DurationUnit.MINUTES)))
            .replace("%m", "${getModuloCurrentTime(DurationUnit.MINUTES)}")
            .replace("%S", "%02d".format(getModuloCurrentTime(DurationUnit.SECONDS)))
            .replace("%s", "${getModuloCurrentTime(DurationUnit.SECONDS)}")
            .replace("%L", "%03d".format(getModuloCurrentTime(DurationUnit.MILLISECONDS)))
            .replace("%l", "${getModuloCurrentTime(DurationUnit.MILLISECONDS)}")
    }

    override fun toString(): String {
        return if (getCurrentTime(DurationUnit.HOURS) > 0) format("%h:%M:%S") else format("%m:%S")
    }
}
