package io.zkz.mc.gametools.scoreboard

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.team.TeamService
import io.zkz.mc.gametools.team.event.TeamChangeEvent
import io.zkz.mc.gametools.team.event.TeamConfigChangeEvent
import io.zkz.mc.gametools.team.event.TeamCreateEvent
import io.zkz.mc.gametools.team.event.TeamRemoveEvent
import io.zkz.mc.gametools.util.BukkitUtils.forEachPlayer
import io.zkz.mc.gametools.util.mm
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.util.UUID

@Injectable
class ScoreboardService(
    plugin: GameToolsPlugin,
    private val teamService: TeamService,
) : PluginService<GameToolsPlugin>(plugin) {
    private var globalScoreboard: GameScoreboard? = null
    private val teamScoreboards = mutableMapOf<String, GameScoreboard>()
    private val playerScoreboards = mutableMapOf<UUID, GameScoreboard>()

    fun setGlobalScoreboard(scoreboard: GameScoreboard?, cleanup: Boolean = false) {
        // Cleanup existing scoreboard, if applicable
        if (cleanup &&
            globalScoreboard != null &&
            teamScoreboards.values.none { it == globalScoreboard } &&
            playerScoreboards.values.none { it == globalScoreboard }
        ) {
            globalScoreboard!!.cleanup()
        }

        // Setup the new scoreboard, just in case
        if (scoreboard != null) {
            setupGlobalTeamsOnScoreboard(scoreboard.getScoreboard())
            updatePlayerTeamMembershipOnScoreboard(scoreboard.getScoreboard())
        }
        globalScoreboard = scoreboard

        // Update clients
        forEachPlayer(this::updatePlayerScoreboardOnClientSide)
    }

    fun setTeamScoreboard(teamId: String, scoreboard: GameScoreboard?, cleanup: Boolean = true) {
        // Cleanup existing scoreboard, if applicable
        val current = teamScoreboards[teamId]
        if (cleanup &&
            current != null &&
            globalScoreboard != current &&
            teamScoreboards.none { it.key != teamId && it.value == current } &&
            playerScoreboards.values.none { it == current }
        ) {
            current.cleanup()
        }

        // Setup the new scoreboard, just in case
        if (scoreboard != null) {
            setupGlobalTeamsOnScoreboard(scoreboard.getScoreboard())
            updatePlayerTeamMembershipOnScoreboard(scoreboard.getScoreboard())
            teamScoreboards[teamId] = scoreboard
        } else {
            teamScoreboards.remove(teamId)
        }

        // Update clients
        forEachPlayer(this::updatePlayerScoreboardOnClientSide)
    }

    fun setPlayerScoreboard(uuid: UUID, scoreboard: GameScoreboard?, cleanup: Boolean = true) {
        // Cleanup existing scoreboard, if applicable
        val current = playerScoreboards[uuid]
        if (cleanup &&
            current != null &&
            globalScoreboard != current &&
            teamScoreboards.values.none { it == current } &&
            playerScoreboards.none { it.key != uuid && it.value == current }
        ) {
            current.cleanup()
        }

        // Setup the new scoreboard, just in case
        if (scoreboard != null) {
            setupGlobalTeamsOnScoreboard(scoreboard.getScoreboard())
            updatePlayerTeamMembershipOnScoreboard(scoreboard.getScoreboard())
            playerScoreboards[uuid] = scoreboard
        } else {
            playerScoreboards.remove(uuid)
        }

        // Update client
        Bukkit.getPlayer(uuid)?.let {
            updatePlayerScoreboardOnClientSide(it)
        }
    }

    val allScoreboards: Set<GameScoreboard>
        get() {
            val scoreboards: MutableSet<GameScoreboard> = HashSet()
            if (globalScoreboard != null) {
                scoreboards.add(globalScoreboard!!)
            }
            teamScoreboards.values.forEach {
                scoreboards.add(it)
            }
            playerScoreboards.values.forEach {
                scoreboards.add(it)
            }
            return scoreboards.toSet()
        }

    fun createNewScoreboard(title: Component): GameScoreboard {
        val scoreboard = GameScoreboard(title)
        setupGlobalTeamsOnScoreboard(scoreboard.getScoreboard())
        updatePlayerTeamMembershipOnScoreboard(scoreboard.getScoreboard())
        return scoreboard
    }

    private fun updatePlayerScoreboardOnClientSide(player: Player) {
        // Choose the scoreboard
        var playerScoreboard = playerScoreboards[player.uniqueId]
        if (playerScoreboard == null) {
            val team = teamService.getTeamOfPlayer(player)
            if (team != null) {
                playerScoreboard = teamScoreboards[team.id]
            }
            if (playerScoreboard == null) {
                playerScoreboard = globalScoreboard
            }
        }

        // Apply the scoreboard
        if (playerScoreboard != null) {
            player.scoreboard = playerScoreboard.getScoreboard()
        } else {
            player.scoreboard = Bukkit.getScoreboardManager().mainScoreboard
        }
    }

    private fun setupGlobalTeamsOnAllScoreboards() {
        setupGlobalTeamsOnScoreboard(Bukkit.getScoreboardManager().mainScoreboard)
        allScoreboards.forEach {
            setupGlobalTeamsOnScoreboard(
                it.getScoreboard(),
            )
        }
        updatePlayerTeamMembershipOnAllScoreboards()
    }

    private fun setupGlobalTeamsOnScoreboard(scoreboard: Scoreboard) {
        teamService.allTeams.forEach { gameTeam ->
            // Remove old team, if exists
            scoreboard.getTeam(gameTeam.id)?.unregister()

            // Create new team
            val team: Team = scoreboard.registerNewTeam(gameTeam.id)
            team.prefix(mm(gameTeam.formatTag + "<0> ", gameTeam.prefix))
            team.color(gameTeam.scoreboardColor)
            team.suffix(mm(""))
            team.setCanSeeFriendlyInvisibles(true)
            team.setAllowFriendlyFire(teamService.friendlyFire)
            team.setOption(Team.Option.COLLISION_RULE, teamService.collisionRule)
        }
    }

    private fun updatePlayerTeamMembershipOnAllScoreboards() {
        updatePlayerTeamMembershipOnScoreboard(Bukkit.getScoreboardManager().mainScoreboard)
        allScoreboards.forEach {
            updatePlayerTeamMembershipOnScoreboard(
                it.getScoreboard(),
            )
        }
    }

    private fun updatePlayerTeamMembershipOnScoreboard(scoreboard: Scoreboard) {
        teamService.allTeams.forEach { gameTeam ->
            val team: Team? = scoreboard.getTeam(gameTeam.id)
            if (team != null) {
                team.entries.forEach(team::removeEntry)
                gameTeam.members.forEach { uuid ->
                    val offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(uuid)
                    if (offlinePlayer.name == null) {
                        return@forEach
                    }
                    team.addEntry(offlinePlayer.name!!)
                }
            }
        }
        forEachPlayer { updatePlayerScoreboardOnClientSide(it) }
    }

    fun resetAllScoreboards() {
        allScoreboards.forEach(GameScoreboard::cleanup)
        globalScoreboard = null
        teamScoreboards.clear()
        playerScoreboards.clear()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        updatePlayerScoreboardOnClientSide(event.player)
        setupGlobalTeamsOnAllScoreboards()
    }

    @EventHandler
    private fun onTeamCreate(event: TeamCreateEvent) {
        // Setup team colors on scoreboards
        setupGlobalTeamsOnAllScoreboards()
    }

    @EventHandler
    private fun onTeamRemove(event: TeamRemoveEvent) {
        // Remove obsolete team scoreboards
        event.teams.map(GameTeam::id).forEach { setTeamScoreboard(it, null) }

        // Setup team colors on scoreboards
        setupGlobalTeamsOnAllScoreboards()
    }

    @EventHandler
    private fun onTeamChange(event: TeamChangeEvent) {
        // Potentially update displayed scoreboard
        event.players
            .mapNotNull { Bukkit.getPlayer(it) }
            .forEach(::updatePlayerScoreboardOnClientSide)

        // Update player colors on scoreboards
        setupGlobalTeamsOnAllScoreboards()
    }

    @EventHandler
    private fun onTeamConfigChange(event: TeamConfigChangeEvent) {
        // Update friendly fire and collision rules
        setupGlobalTeamsOnAllScoreboards()
    }
}
