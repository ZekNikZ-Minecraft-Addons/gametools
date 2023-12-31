package io.zkz.mc.gametools.teams.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.teams.GameTeam
import java.util.*

class TeamCreateEvent : AbstractEvent {
    val teams: List<GameTeam>

    constructor(teams: Collection<GameTeam>) {
        this.teams = teams.toList()
    }

    constructor(vararg teams: GameTeam) {
        this.teams = listOf(*teams)
    }
}
