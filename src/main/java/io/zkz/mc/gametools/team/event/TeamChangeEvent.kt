package io.zkz.mc.gametools.team.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.team.GameTeam
import java.util.*

class TeamChangeEvent : AbstractEvent {
    val source: TeamEventSource
    val players: List<UUID>
    val oldTeam: GameTeam?
    val newTeam: GameTeam?

    constructor(source: TeamEventSource, oldTeam: GameTeam?, newTeam: GameTeam?, players: Collection<UUID>) {
        this.source = source
        this.oldTeam = oldTeam
        this.newTeam = newTeam
        this.players = players.toList()
    }

    constructor(source: TeamEventSource, oldTeam: GameTeam?, newTeam: GameTeam?, vararg players: UUID) {
        this.source = source
        this.oldTeam = oldTeam
        this.newTeam = newTeam
        this.players = listOf(*players)
    }
}
