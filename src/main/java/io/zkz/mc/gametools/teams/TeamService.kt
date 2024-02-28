package io.zkz.mc.gametools.teams

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.event.event
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.get
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.teams.event.TeamChangeEvent
import io.zkz.mc.gametools.teams.event.TeamConfigChangeEvent
import io.zkz.mc.gametools.teams.event.TeamCreateEvent
import io.zkz.mc.gametools.teams.event.TeamEventSource
import io.zkz.mc.gametools.teams.event.TeamRemoveEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team.OptionStatus
import java.util.*

@Injectable
class TeamService(
    plugin: GameToolsPlugin,
) : PluginService<GameToolsPlugin>(plugin) {
    // #region Team Data
    private val teams = mutableMapOf<String, GameTeam>()
    private val players = mutableMapOf<UUID, String>()
    private var _teamConfig = TeamConfig(
        friendlyFire = false,
        glowingEnabled = true,
        collisionRule = OptionStatus.NEVER,
    )
    // #endregion

    // #region Team Configs
    var friendlyFire: Boolean
        get() = _teamConfig.friendlyFire
        set(value) {
            _teamConfig.friendlyFire = value
            event(TeamConfigChangeEvent(_teamConfig))
            // TODO: handle this in an event handler
            //  ScoreboardService.getInstance().setupGlobalTeams()
        }

    var glowingEnabled: Boolean
        get() = _teamConfig.glowingEnabled
        set(value) {
            _teamConfig.glowingEnabled = value
            event(TeamConfigChangeEvent(_teamConfig))
            // TODO: handle this in an event handler
            //  BukkitUtils.forEachPlayer {
            //      VanishingService.getInstance().hidePlayer(it, "glow-refresh")
            //      VanishingService.getInstance().showPlayer(it, "glow-refresh")
            //  }
        }

    var collisionRule: OptionStatus
        get() = _teamConfig.collisionRule
        set(value) {
            _teamConfig.collisionRule = value
            event(TeamConfigChangeEvent(_teamConfig))
            // TODO: handle this in an event handler
            //  ScoreboardService.getInstance().setupGlobalTeams()
        }
    // #endregion

    // #region Team Queries
    val allTeams: Collection<GameTeam>
        get() = teams.values

    val allNonSpectatorTeams: Collection<GameTeam>
        get() = teams.values.filter { team: GameTeam -> !team.isSpectator }

    fun getTeam(id: String): GameTeam? = teams[id]
    // #endregion

    // #region Player Queries
    val areAllNonSpectatorsOnline: Boolean
        get() = trackedPlayers.filter {
            val team = getTeamOfPlayer(it)
            return team != null && !team.isSpectator
        }.all { Bukkit.getPlayer(it) != null }

    val trackedPlayers: Collection<UUID>
        get() = players.keys

    fun areAllSameTeam(players: Collection<UUID>): Boolean {
        if (players.isEmpty()) {
            return true
        }
        var flag = false
        var first: GameTeam? = null
        for (playerId in players) {
            if (!flag) {
                first = getTeamOfPlayer(playerId)
                flag = true
                continue
            }
            if (getTeamOfPlayer(playerId) != first) {
                return false
            }
        }
        return true
    }

    fun getTeamOfPlayer(player: Player): GameTeam? {
        return getTeamOfPlayer(player.uniqueId)
    }

    fun getTeamOfPlayer(playerId: UUID): GameTeam? {
        val teamId = players[playerId]
        return if (teamId == null) null else teams[teamId]
    }

    fun getTeamMembers(teamId: String): Collection<UUID> {
        return players.entries.filter { teamId == it.value }.map { it.key }
    }

    fun getTeamMembers(team: GameTeam): Collection<UUID> {
        return getTeamMembers(team.id)
    }

    fun getOnlineTeamMembers(teamId: String): Collection<Player> {
        return players.entries.asSequence().filter { teamId == it.value }.map { it.key }
            .map { Bukkit.getPlayer(it) }.filter { Objects.nonNull(it) }.map { it!! }.toList()
    }

    fun getOnlineTeamMembers(team: GameTeam): Collection<Player> {
        return getOnlineTeamMembers(team.id)
    }
    // #endregion

    // #region Team Creation
    class TeamCreationException(problem: String) : RuntimeException("Could not create team: $problem")

    @Throws(TeamCreationException::class)
    fun createTeam(team: GameTeam, source: TeamEventSource = TeamEventSource.TEAM_API, suppressEvent: Boolean = false) {
        // Ensure that the team does not already exist
        if (teams.containsKey(team.id)) {
            throw TeamCreationException("team ID already exists")
        }

        // Save the team
        teams[team.id] = team

        // Call event
        if (!suppressEvent) {
            event(TeamCreateEvent(source, team))
        }
    }

    fun setupDefaultTeams() {
        // Clear the existing teams
        removeAllTeams()

        // Add all the default teams
        get<DefaultTeams>().addAll()

        // Call event
        event(TeamCreateEvent(TeamEventSource.TEAM_API, teams.values))
    }
    // #endregion

    // #region Team Deletion
    fun removeTeam(team: GameTeam, source: TeamEventSource = TeamEventSource.TEAM_API) {
        removeTeam(team.id, source)
    }

    fun removeTeam(id: String, source: TeamEventSource = TeamEventSource.TEAM_API) {
        // If the team doesn't exist, this is a no-op
        if (!teams.containsKey(id)) {
            return
        }

        // Remove the team
        val team: GameTeam = teams.remove(id) ?: return
        clearTeam(id)

        // Call event
        event(TeamRemoveEvent(source, team))
    }

    fun removeAllTeams(source: TeamEventSource = TeamEventSource.TEAM_API) {
        // Clear players
        clearAllPlayersFromTeams(source)

        // Clear teams
        if (teams.isNotEmpty()) {
            event(TeamRemoveEvent(source, teams.values))
        }
        teams.clear()
    }
    // #endregion

    // #region Player Team Joining
    class TeamJoinException(problem: String) : RuntimeException("Could not join team: $problem")

    @Throws(TeamJoinException::class)
    fun joinTeam(
        player: Player,
        team: GameTeam,
        source: TeamEventSource = TeamEventSource.TEAM_API,
        suppressEvent: Boolean = false,
    ) {
        joinTeam(player.uniqueId, team.id, source, suppressEvent)
    }

    @Throws(TeamJoinException::class)
    fun joinTeam(
        playerId: UUID,
        teamId: String,
        source: TeamEventSource = TeamEventSource.TEAM_API,
        suppressEvent: Boolean = false,
    ) {
        val newTeam = getTeam(teamId) ?: throw TeamJoinException("Team does not exist")

        // Get old team
        val oldTeam = getTeamOfPlayer(playerId)

        // Join the team
        players[playerId] = teamId

        // Call event
        if (!suppressEvent) {
            event(TeamChangeEvent(source, oldTeam, newTeam, playerId))
        }
    }
    // #endregion

    // #region Player Team Leaving
    fun leaveTeam(player: Player, source: TeamEventSource = TeamEventSource.TEAM_API) {
        leaveTeam(player.uniqueId, source)
    }

    fun leaveTeam(playerId: UUID, source: TeamEventSource = TeamEventSource.TEAM_API) {
        // Ensure the player is already on a team
        val currentTeam: String = players[playerId] ?: return

        // Leave the team
        players.remove(playerId)

        // Call event
        event(TeamChangeEvent(source, getTeam(currentTeam), null, playerId))
    }

    fun clearTeam(teamId: String, source: TeamEventSource = TeamEventSource.TEAM_API) {
        // Ensure the team exists
        val team = getTeam(teamId) ?: return

        // Remove the team members
        val teamMembers = getTeamMembers(teamId)
        if (teamMembers.isEmpty()) {
            return
        }
        teamMembers.forEach {
            players.remove(it)
        }

        // Call event
        event(TeamChangeEvent(source, team, null, teamMembers))
    }

    /**
     * Remove all players from teams.
     */
    fun clearAllPlayersFromTeams(source: TeamEventSource = TeamEventSource.TEAM_API) {
        teams.keys.forEach { teamId: String -> clearTeam(teamId, source) }
    }
    // #endregion
}
