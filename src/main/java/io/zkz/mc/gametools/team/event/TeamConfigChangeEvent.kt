package io.zkz.mc.gametools.team.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.team.TeamConfig

class TeamConfigChangeEvent(val newConfig: TeamConfig) : AbstractEvent()
