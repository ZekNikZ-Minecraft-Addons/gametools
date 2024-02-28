package io.zkz.mc.gametools.score

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.teams.GameTeam

class TeamEarnPointsEvent(val team: GameTeam, val scoreEntry: ScoreEntry) : AbstractEvent()
