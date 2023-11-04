package io.zkz.mc.gametools.util

import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.GameRule
import org.bukkit.World
import java.util.function.Consumer

object WorldSyncUtils {
    fun <T : Any> setGameRule(rule: GameRule<T>, value: T) {
        Bukkit.getWorlds().forEach { it.setGameRule(rule, value) }
    }

    fun setWorldBorderCenter(x: Double, y: Double) {
        Bukkit.getWorlds().forEach {
            it.worldBorder.setCenter(
                if (it.environment == World.Environment.NETHER) x / 8 else x,
                if (it.environment == World.Environment.NETHER) y / 8 else y,
            )
        }
    }

    fun setWorldBorderSize(newSize: Double, seconds: Long) {
        Bukkit.getWorlds().forEach { it.worldBorder.setSize(if (it.environment == World.Environment.NETHER) newSize / 8 else newSize, seconds) }
    }

    fun setWorldBorderWarningTime(seconds: Int) {
        Bukkit.getWorlds().forEach { it.worldBorder.warningTime = seconds }
    }

    fun setWorldBorderWarningDistance(distance: Int) {
        Bukkit.getWorlds().forEach { it.worldBorder.warningDistance = distance }
    }

    fun setWorldBorderDamageAmount(damage: Double) {
        Bukkit.getWorlds().forEach { it.worldBorder.damageAmount = damage }
    }

    fun setWorldBorderDamageBuffer(blocks: Double) {
        Bukkit.getWorlds().forEach { it.worldBorder.damageBuffer = blocks }
    }

    fun resetWorldBorder() {
        Bukkit.getWorlds().forEach { it.worldBorder.reset() }
    }

    var worldBorderSize: Double
        get() = Bukkit.getWorlds()[0].worldBorder.size
        set(newSize) {
            Bukkit.getWorlds().forEach { it.worldBorder.size = if (it.environment == World.Environment.NETHER) newSize / 8 else newSize }
        }

    fun stopWorldBorder() {
        worldBorderSize = worldBorderSize
    }

    fun setDifficulty(difficulty: Difficulty) {
        Bukkit.getWorlds().forEach { it.difficulty = difficulty }
    }

    fun setTime(time: Int) {
        Bukkit.getWorlds().forEach { it.time = time.toLong() }
    }

    fun setWeatherClear() {
        Bukkit.getWorlds().forEach(
            Consumer { world: World ->
                world.setStorm(false)
                world.isThundering = false
                world.weatherDuration = 0
            },
        )
    }

    fun setWeatherRain() {
        Bukkit.getWorlds().forEach(
            Consumer { world: World ->
                world.setStorm(true)
                world.isThundering = false
            },
        )
    }

    fun setWeatherStorm() {
        Bukkit.getWorlds().forEach(
            Consumer { world: World ->
                world.setStorm(true)
                world.isThundering = true
            },
        )
    }

    val time: Long
        get() = Bukkit.getWorlds()[0].time
}
