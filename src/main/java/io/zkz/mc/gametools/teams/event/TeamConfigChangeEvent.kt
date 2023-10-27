package io.zkz.mc.gametools.teams.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.teams.TeamConfig

class TeamConfigChangeEvent(val newConfig: TeamConfig) : AbstractEvent()