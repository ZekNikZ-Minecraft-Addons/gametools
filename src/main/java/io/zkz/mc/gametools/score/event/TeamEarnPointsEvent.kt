package io.zkz.mc.gametools.score.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.score.ScoreEntry
import io.zkz.mc.gametools.team.GameTeam

class TeamEarnPointsEvent(val team: GameTeam, val scoreEntry: ScoreEntry) : AbstractEvent()
