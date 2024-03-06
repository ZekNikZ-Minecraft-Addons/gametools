package io.zkz.mc.gametools.team

import org.bukkit.scoreboard.Team.OptionStatus

data class TeamConfig(
    var friendlyFire: Boolean,
    var glowingEnabled: Boolean,
    var collisionRule: OptionStatus,
)
