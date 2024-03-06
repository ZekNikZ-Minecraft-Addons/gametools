package io.zkz.mc.gametools.score.event

import io.zkz.mc.gametools.event.AbstractEvent

class MultiplierChangeEvent(
    val oldMultiplier: Double,
    val newMultiplier: Double,
) : AbstractEvent()
