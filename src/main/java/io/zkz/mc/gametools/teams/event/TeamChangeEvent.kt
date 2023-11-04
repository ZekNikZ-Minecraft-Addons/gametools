package io.zkz.mc.gametools.teams.event

import io.zkz.mc.gametools.event.AbstractEvent
import io.zkz.mc.gametools.teams.GameTeam
import java.util.*

class TeamChangeEvent : AbstractEvent {
    val players: List<UUID>
    val oldTeam: GameTeam?
    val newTeam: GameTeam?

    constructor(oldTeam: GameTeam?, newTeam: GameTeam?, players: Collection<UUID>) {
        this.oldTeam = oldTeam
        this.newTeam = newTeam
        this.players = players.toList()
    }

    constructor(oldTeam: GameTeam?, newTeam: GameTeam?, vararg players: UUID) {
        this.oldTeam = oldTeam
        this.newTeam = newTeam
        this.players = listOf(*players)
    }
}
