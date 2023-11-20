package io.zkz.mc.gametools.teams

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.util.BlockUtils
import io.zkz.mc.gametools.util.GTColor
import io.zkz.mc.gametools.util.mm
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

data class GameTeam(
    val id: String,
    val name: Component,
    val prefix: Component,
    val formatTag: String,
    val color: GTColor,
    val scoreboardColor: NamedTextColor,
    val isSpectator: Boolean = false,
) : ForwardingAudience, InjectionComponent {
    private val teamService by inject<TeamService>()

    val displayName: Component
        get() = mm("$formatTag<0> <1>", prefix, name)

    val woolColor: Material
        get() = BlockUtils.getWoolColor(scoreboardColor)!!

    val concreteColor: Material
        get() = BlockUtils.getConcreteColor(scoreboardColor)!!

    val members: Collection<UUID>
        get() = teamService.getTeamMembers(this)

    val onlineMembers: Collection<Player>
        get() {
            return teamService.getOnlineTeamMembers(this)
        }

    fun removeAllMembers() {
        teamService.clearTeam(id)
    }

    fun addMember(player: Player) {
        teamService.joinTeam(player, this)
    }

    fun addMember(playerId: UUID) {
        teamService.joinTeam(playerId, id)
    }

    operator fun contains(player: Player): Boolean {
        return this == teamService.getTeamOfPlayer(player)
    }

    operator fun contains(playerId: UUID): Boolean {
        return this == teamService.getTeamOfPlayer(playerId)
    }

    override fun audiences(): Iterable<Audience> = onlineMembers
}
