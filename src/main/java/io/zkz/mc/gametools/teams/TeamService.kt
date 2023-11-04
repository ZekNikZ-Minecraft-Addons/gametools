package io.zkz.mc.gametools.teams

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.event.event
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.get
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.teams.event.TeamChangeEvent
import io.zkz.mc.gametools.teams.event.TeamConfigChangeEvent
import io.zkz.mc.gametools.teams.event.TeamCreateEvent
import io.zkz.mc.gametools.teams.event.TeamRemoveEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team.OptionStatus
import java.util.*

@Injectable
class TeamService(
    plugin: GameToolsPlugin,
) : PluginService<GameToolsPlugin>(plugin) {
    private val teams: MutableMap<String, GameTeam> get() = mutableMapOf()
    private val players: MutableMap<UUID, String> get() = mutableMapOf()

    private var _teamConfig = TeamConfig(
        friendlyFire = false,
        glowingEnabled = true,
        collisionRule = OptionStatus.NEVER,
    )

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

    val areAllNonSpectatorsOnline: Boolean
        get() = trackedPlayers.filter {
            val team = getTeamOfPlayer(it)
            return team != null && !team.isSpectator
        }.all { Bukkit.getPlayer(it) != null }

    val trackedPlayers: Collection<UUID>
        get() = players.keys

    val allTeams: Collection<GameTeam>
        get() = teams.values

    val allNonSpectatorTeams: Collection<GameTeam>
        get() = teams.values.filter { team: GameTeam -> !team.isSpectator }

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

    class TeamCreationException(problem: String) : RuntimeException("Could not create team: $problem")

    @Throws(TeamCreationException::class)
    fun createTeam(team: GameTeam) = createTeam(team, false)

    @Throws(TeamCreationException::class)
    fun createTeam(team: GameTeam, suppressEvent: Boolean) {
        // Ensure that the team does not already exist
        if (teams.containsKey(team.id)) {
            throw TeamCreationException("team ID already exists")
        }

        // Save the team
        teams[team.id] = team

        // Call event
        if (!suppressEvent) {
            Bukkit.getServer().pluginManager.callEvent(TeamCreateEvent(team))
        }
    }

    fun setupDefaultTeams() {
        // Clear the existing teams
        clearTeams()

        // Add all the default teams
        get<DefaultTeams>().addAll()

        // Call event
        Bukkit.getServer().pluginManager.callEvent(TeamCreateEvent(teams.values))
    }

    fun removeTeam(id: String) {
        // If the team doesn't exist, this is a no-op
        if (!teams.containsKey(id)) {
            return
        }

        // Remove the team
        val team: GameTeam = teams.remove(id) ?: return
        clearTeam(id)

        // Call event
        Bukkit.getServer().pluginManager.callEvent(TeamRemoveEvent(team))
    }

    fun getTeam(id: String?): GameTeam? = teams[id]

    fun clearTeams() {
        // Clear players
        clearAllPlayersFromTeams()

        // Clear teams
        if (teams.isNotEmpty()) {
            Bukkit.getServer().pluginManager.callEvent(TeamRemoveEvent(teams.values))
        }
        teams.clear()
    }

    fun clearTeam(teamId: String) {
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
        Bukkit.getServer().pluginManager.callEvent(
            TeamChangeEvent(
                team,
                null,
                teamMembers,
            ),
        )
    }

    /**
     * Remove all players from teams.
     */
    fun clearAllPlayersFromTeams() {
        teams.keys.forEach { teamId: String -> clearTeam(teamId) }
    }

    class TeamJoinException(problem: String) : RuntimeException("Could not join team: $problem")

    @Throws(TeamJoinException::class)
    fun joinTeam(player: Player, team: GameTeam) {
        joinTeam(player.uniqueId, team.id)
    }

    @Throws(TeamJoinException::class)
    fun joinTeam(player: Player, team: GameTeam, suppressEvent: Boolean) {
        joinTeam(player.uniqueId, team.id, suppressEvent)
    }

    @Throws(TeamJoinException::class)
    fun joinTeam(playerId: UUID, teamId: String?) {
        joinTeam(playerId, teamId, false)
    }

    @Throws(TeamJoinException::class)
    fun joinTeam(playerId: UUID, teamId: String?, suppressEvent: Boolean) {
        val newTeam = getTeam(teamId) ?: throw TeamJoinException("Team does not exist")

        // Get old team
        val oldTeam = getTeamOfPlayer(playerId)

        // Join the team
        players[playerId] = teamId!!

        // Call event
        if (!suppressEvent) {
            Bukkit.getServer().pluginManager.callEvent(
                TeamChangeEvent(
                    oldTeam,
                    newTeam,
                    playerId,
                ),
            )
        }
    }

    fun leaveTeam(player: Player) {
        leaveTeam(player.uniqueId)
    }

    fun leaveTeam(playerId: UUID) {
        // Ensure the player is already on a team
        val currentTeam: String = players[playerId] ?: return

        // Leave the team
        players.remove(playerId)

        // Call event
        Bukkit.getServer().pluginManager.callEvent(
            TeamChangeEvent(
                getTeam(currentTeam),
                null,
                playerId,
            ),
        )
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
}
