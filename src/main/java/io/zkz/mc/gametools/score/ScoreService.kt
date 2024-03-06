package io.zkz.mc.gametools.score

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.event.event
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.score.event.MultiplierChangeEvent
import io.zkz.mc.gametools.score.event.PlayerEarnPointsEvent
import io.zkz.mc.gametools.score.event.TeamEarnPointsEvent
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.team.GameTeam
import io.zkz.mc.gametools.team.TeamService
import io.zkz.mc.gametools.util.GTConstants
import io.zkz.mc.gametools.util.observable.IObservable
import io.zkz.mc.gametools.util.observable.IObserver
import org.bukkit.entity.Player
import java.util.*

@Injectable
class ScoreService(
    plugin: GameToolsPlugin,
    private val constants: GTConstants,
    private val teamService: TeamService,
) : PluginService<GameToolsPlugin>(plugin), IObservable<ScoreService> {
    private var entries: MutableList<ScoreEntry> = mutableListOf()
    var currentScoreMultiplier = 1.0
        set(newVal) {
            val oldVal = field
            field = newVal
            event(MultiplierChangeEvent(oldVal, newVal))
        }

    // #region Earn Points
    fun earnPoints(
        playerId: UUID,
        reason: String,
        points: Double,
        teamId: String? = null,
        roundIndex: Int? = null,
        gameId: String? = null,
    ) {
        val playerTeam = teamService.getTeamOfPlayer(playerId)
        val entry = ScoreEntry(
            playerId,
            teamId ?: playerTeam?.id,
            gameId ?: constants.gameId,
            roundIndex,
            reason,
            points,
            currentScoreMultiplier,
        )
        entries.add(entry)

        event(PlayerEarnPointsEvent(entry))
        if (playerTeam != null) {
            event(TeamEarnPointsEvent(playerTeam, entry))
        }

        notifyObservers()
    }

    fun earnPoints(
        player: Player,
        reason: String,
        points: Double,
        team: GameTeam? = null,
        roundIndex: Int? = null,
        gameId: String? = null,
    ) {
        earnPoints(player.uniqueId, reason, points, team?.id, roundIndex, gameId)
    }

    fun earnPoints(
        playerIds: Collection<UUID>,
        reason: String,
        points: Double,
        teamId: String?,
        roundIndex: Int? = null,
        gameId: String? = null,
    ) {
        playerIds.forEach {
            val playerTeam = teamService.getTeamOfPlayer(it)
            val entry = ScoreEntry(
                it,
                teamId ?: teamService.getTeamOfPlayer(it)?.id,
                gameId ?: constants.gameId,
                roundIndex,
                reason,
                points,
                currentScoreMultiplier,
            )
            entries.add(entry)

            event(PlayerEarnPointsEvent(entry))
            if (playerTeam != null) {
                event(TeamEarnPointsEvent(playerTeam, entry))
            }
        }

        notifyObservers()
    }

    fun earnPoints(
        players: Collection<Player>,
        reason: String,
        points: Double,
        team: GameTeam? = null,
        roundIndex: Int? = null,
        gameId: String? = null,
    ) {
        earnPoints(players.map { it.uniqueId }, reason, points, team?.id, roundIndex, gameId)
    }
    // #endregion

    // #region Score Queries
    val allEntries
        get() = entries.toList()

    fun query(): ScoreQuery {
        return ScoreQuery()
    }
    // #endregion

    // #region IObservable Implementation
    override val listeners: MutableList<IObserver<ScoreService>> = mutableListOf()

    override fun addListener(observer: IObserver<ScoreService>) {
        listeners.add(observer)
    }

    override fun removeListener(observer: IObserver<ScoreService>) {
        listeners.remove(observer)
    }
    // #endregion

    // #region Remote Sync
    fun setEntries(entries: List<ScoreEntry>) {
        this.entries = entries.toMutableList()
        notifyObservers()
    }
    // #endregion
}
