package io.zkz.mc.gametools.score

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.teams.GameTeam
import io.zkz.mc.gametools.teams.TeamService
import io.zkz.mc.gametools.util.GTConstants
import org.bukkit.entity.Player
import java.util.*

class ScoreQuery : InjectionComponent {
    private val teamService by inject<TeamService>()

    fun forPlayer(player: Player): Stage2<Double> {
        return forPlayer(player.uniqueId)
    }

    fun forPlayer(playerId: UUID): Stage2<Double> {
        return Stage2 { entries ->
            entries
                .filter { it.playerId == playerId }
                .sumOf { it.totalPoints }
        }
    }

    fun forTeam(team: GameTeam): Stage2<Double> {
        return forTeam(team.id)
    }

    fun forTeam(teamId: String): Stage2<Double> {
        return Stage2 { entries ->
            entries
                .filter { it.teamId == teamId }
                .sumOf { it.totalPoints }
        }
    }

    fun forAllPlayers(): Stage2<Map<UUID, Double>> {
        return Stage2 { entries ->
            entries
                .groupBy { it.playerId }
                .mapValues { entry -> entry.component2().sumOf { it.totalPoints } }
        }
    }

    fun forAllTeams(): Stage2<Map<String?, Double>> {
        return Stage2 { entries ->
            entries
                .groupBy { it.teamId }
                .mapValues { entry -> entry.component2().sumOf { it.totalPoints } }
        }
    }

    class Stage2<T>(val reducer: (entries: List<ScoreEntry>) -> T) : InjectionComponent {
        private val scoreService by inject<ScoreService>()
        private val constants by inject<GTConstants>()

        private var round: Int? = null
        private var minigame: String? = null
        private var wholeEvent: Boolean = true

        fun inRound(round: Int): Stage2<T> {
            this.round = round
            wholeEvent = false
            return this
        }

        fun inGame(minigame: String? = null): Stage2<T> {
            this.minigame = minigame
            wholeEvent = false
            return this
        }

        fun get(): T {
            var entries = scoreService.allEntries

            if (!wholeEvent) {
                entries = entries.filter { it.minigame == (minigame ?: constants.gameId) }

                if (round != null) {
                    entries = entries.filter { it.round == round }
                }
            }

            return reducer(entries)
        }
    }
}
