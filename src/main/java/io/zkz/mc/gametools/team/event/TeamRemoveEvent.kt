package io.zkz.mc.gametools.team.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.team.GameTeam

class TeamRemoveEvent : AbstractEvent {
    val source: TeamEventSource
    val teams: List<GameTeam>

    constructor(source: TeamEventSource, teams: Collection<GameTeam>) {
        this.source = source
        this.teams = teams.toList()
    }

    constructor(source: TeamEventSource, vararg teams: GameTeam) {
        this.source = source
        this.teams = listOf(*teams)
    }
}
