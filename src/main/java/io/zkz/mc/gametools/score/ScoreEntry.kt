package io.zkz.mc.gametools.score

import java.util.*

data class ScoreEntry(
    val playerId: UUID,
    val minigame: String,
    val round: Int,
    val reason: String,
    val points: Double,
    val multiplier: Double,
) {
    val totalPoints: Double
        get() = points * multiplier
}
