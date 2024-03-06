package io.zkz.mc.gametools.score.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.score.ScoreEntry

class PlayerEarnPointsEvent(val scoreEntry: ScoreEntry) : AbstractEvent()
