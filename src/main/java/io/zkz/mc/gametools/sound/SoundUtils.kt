package io.zkz.mc.gametools.sound

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player

fun playSound(sound: Sound, volume: Float, pitch: Float) {
    playSound(Bukkit.getOnlinePlayers(), sound, volume, pitch)
}

fun playSound(player: Player, sound: Sound, volume: Float, pitch: Float) {
    player.playSound(player.location, sound, volume, pitch)
}

fun playSound(players: Collection<Player>, sound: Sound, volume: Float, pitch: Float) {
    players.forEach { playSound(it, sound, volume, pitch) }
}
