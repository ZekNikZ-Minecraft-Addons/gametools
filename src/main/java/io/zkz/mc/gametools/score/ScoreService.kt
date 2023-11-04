package io.zkz.mc.gametools.score

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.teams.GameTeam
import io.zkz.mc.gametools.teams.TeamService
import io.zkz.mc.gametools.util.GTConstants
import io.zkz.mc.gametools.util.observable.IObservable
import io.zkz.mc.gametools.util.observable.IObserver
import org.bukkit.entity.Player
import java.util.*

class ScoreService(
    plugin: GameToolsPlugin,
    private val constants: GTConstants,
    private val teamService: TeamService
) : PluginService<GameToolsPlugin>(plugin), IObservable<ScoreService> {
    private val entries: MutableList<ScoreEntry> = mutableListOf()

    private val roundPlayerScores: MutableMap<UUID, Double> = mutableMapOf()
    private val roundTeamScores: MutableMap<GameTeam?, Double> = mutableMapOf()

    private val gamePlayerScores: MutableMap<UUID, Double> = mutableMapOf()
    private val gameTeamScores: MutableMap<GameTeam?, Double> = mutableMapOf()

    var multiplier = 1.0

    fun earnPoints(playerId: UUID, reason: String, points: Double, roundIndex: Int, gameId: String? = null) {
        val entry = ScoreEntry(
            playerId,
            gameId ?: constants.gameId,
            roundIndex,
            reason,
            points,
            multiplier
        )
        entries.add(entry)

        // Player score
        roundPlayerScores.merge(
            playerId,
            entry.totalPoints
        ) { a, b -> a + b }
        gamePlayerScores.merge(
            playerId,
            entry.totalPoints
        ) { a, b -> a + b }

        // Team score
        val team: GameTeam? = teamService.getTeamOfPlayer(playerId)
        roundTeamScores.merge(
            team,
            entry.totalPoints
        ) { a, b -> a + b }
        gameTeamScores.merge(
            team,
            entry.totalPoints
        ) { a, b -> a + b }
        this.notifyObservers()
    }

    fun earnPoints(player: Player, reason: String, points: Double, roundIndex: Int, gameId: String? = null) {
        this.earnPoints(player.uniqueId, reason, points, roundIndex, gameId)
    }

    fun earnPointsUUID(playerIds: Collection<UUID>, reason: String, points: Double, roundIndex: Int, gameId: String? = null) {
        playerIds.forEach { this.earnPoints(it, reason, points, roundIndex, gameId) }
    }

    fun earnPoints(players: Collection<Player>, reason: String, points: Double, roundIndex: Int, gameId: String? = null) {
        players.forEach { this.earnPoints(it, reason, points, roundIndex, gameId) }
    }

    val roundPlayerScoreSummary: Map<UUID, Double>
        get() = roundPlayerScores

    fun getRoundEntries(player: Player, roundIndex: Int, gameId: String? = null): List<ScoreEntry> {
        return entries
            .filter { it.minigame == (gameId ?: constants.gameId) }
            .filter { it.round == roundIndex }
            .filter { it.playerId == player.uniqueId }
    }

    fun getRoundTeamMemberScoreSummary(team: GameTeam?, roundIndex: Int, gameId: String? = null): Map<UUID, Double> {
        return entries
            .asSequence()
            .filter { it.minigame == (gameId ?: constants.gameId) }
            .filter { it.round == roundIndex }
            .filter { teamService.getTeamOfPlayer(it.playerId) === team }
            .groupingBy { it.playerId }
            .fold(0.0) { acc, it -> acc + it.totalPoints }
    }

    fun getGameEntries(player: Player, gameId: String? = null): List<ScoreEntry> {
        return entries
            .asSequence()
            .filter { it.minigame == (gameId ?: constants.gameId) }
            .filter { it.playerId == player.uniqueId }
            .toList()
    }

    fun getGameTeamMemberScores(team: GameTeam?, gameId: String? = null): Map<UUID, Double> {
        return entries
            .asSequence()
            .filter { it.minigame == (gameId ?: constants.gameId) }
            .filter { teamService.getTeamOfPlayer(it.playerId) === team }
            .groupingBy { it.playerId }
            .fold(0.0) { acc, it -> acc + it.totalPoints }
    }

    val eventTeamScores: Map<GameTeam?, Double>
        get() = entries
            .asSequence()
            .filter { teamService.getTeamOfPlayer(it.playerId) != null }
            .groupingBy { teamService.getTeamOfPlayer(it.playerId) }
            .fold(0.0) { acc, it -> acc + it.totalPoints }

    override val listeners: MutableList<IObserver<ScoreService>> = mutableListOf()

    override fun addListener(observer: IObserver<ScoreService>) {
        listeners.add(observer)
    }

    override fun removeListener(observer: IObserver<ScoreService>) {
        listeners.remove(observer)
    }

    fun resetRoundScores(teams: Collection<GameTeam?>) {
        roundPlayerScores.clear()
        roundTeamScores.clear()
        teams.forEach { roundTeamScores[it] = 0.0 }
    }

    fun resetGameScores(teams: Collection<GameTeam?>) {
        gamePlayerScores.clear()
        gameTeamScores.clear()
        teams.forEach { gameTeamScores[it] = 0.0 }
    }
}
