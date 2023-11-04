package io.zkz.mc.gametools.util

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask


abstract class GameTask protected constructor(private val delay: Long, period: Long?) : BukkitRunnable() {
    private val internalTaskId: Long = nextInternalTaskId++
    private val isRepeating: Boolean
    private val period: Long
    private var task: BukkitTask? = null

    init {
        isRepeating = period != null
        this.period = period ?: -1
    }

    @Synchronized
    fun start(plugin: Plugin) {
        if (isRepeating) {
            this.task = runTaskTimer(plugin, delay, period)
        } else {
            this.task = runTaskLater(plugin, delay)
        }
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    override fun cancel() {
        this.cancel(true)
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    fun cancel(@Suppress("UNUSED_PARAMETER") removeReference: Boolean) {
        super.cancel()
        this.task = null
    }

    @Synchronized
    override fun getTaskId(): Int {
        checkNotNull(this.task) { "Task is not scheduled" }
        return this.task!!.taskId
    }

    @get:Synchronized
    val isScheduled: Boolean
        get() = this.task != null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameTask) return false

        if (internalTaskId != other.internalTaskId) return false

        return true
    }

    override fun hashCode(): Int {
        return internalTaskId.hashCode()
    }

    companion object {
        private var nextInternalTaskId = 0L
    }
}