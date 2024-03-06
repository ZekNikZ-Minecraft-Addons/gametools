package io.zkz.mc.gametools.team

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.get
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.team.TeamService.TeamCreationException
import io.zkz.mc.gametools.util.GTColor
import io.zkz.mc.gametools.util.mm
import net.kyori.adventure.text.format.NamedTextColor
import java.util.logging.Level

@Injectable
object DefaultTeams : InjectionComponent {
    private val teamService by inject<TeamService>()
    private val logger by lazy { get<GameToolsPlugin>().logger }

    val SPECTATOR = GameTeam(
        id = "spectators",
        name = mm("Spectators"),
        prefix = mm("<dark_gray>[SPEC]"),
        formatTag = "<light_gray>",
        color = GTColor.LIGHT_GRAY,
        scoreboardColor = NamedTextColor.GRAY,
        isSpectator = true,
    )

    val GAME_MASTER = GameTeam(
        id = "game_masters",
        name = mm("Game Masters"),
        prefix = mm("<alert_accent>[GM]"),
        formatTag = "<light_gray>",
        color = GTColor.LIGHT_GRAY,
        scoreboardColor = NamedTextColor.GRAY,
        isSpectator = true,
    )

    val CASTER = GameTeam(
        id = "casters",
        name = mm("Casters"),
        prefix = mm("<purple>[CASTER]"),
        formatTag = "<light_gray>",
        color = GTColor.LIGHT_GRAY,
        scoreboardColor = NamedTextColor.GRAY,
        isSpectator = true,
    )

    val BLUE: GameTeam = GameTeam(
        id = "blue",
        name = mm("Blue Team"),
        prefix = mm("<blue><bold>B"),
        formatTag = "<blue>",
        color = GTColor.BLUE,
        scoreboardColor = NamedTextColor.BLUE,
    )

    val RED: GameTeam = GameTeam(
        id = "red",
        name = mm("Red Team"),
        prefix = mm("<red><bold>R"),
        formatTag = "<red>",
        color = GTColor.RED,
        scoreboardColor = NamedTextColor.RED,
    )

    val GREEN: GameTeam = GameTeam(
        id = "green",
        name = mm("Green Team"),
        prefix = mm("<green><bold>G"),
        formatTag = "<green>",
        color = GTColor.GREEN,
        scoreboardColor = NamedTextColor.DARK_GREEN,
    )

    val YELLOW: GameTeam = GameTeam(
        id = "yellow",
        name = mm("Yellow Team"),
        prefix = mm("<yellow><bold>Y"),
        formatTag = "<yellow>",
        color = GTColor.YELLOW,
        scoreboardColor = NamedTextColor.YELLOW,
    )

    val MAGENTA: GameTeam = GameTeam(
        id = "magenta",
        name = mm("Magenta Team"),
        prefix = mm("<magenta><bold>M"),
        formatTag = "<magenta>",
        color = GTColor.MAGENTA,
        scoreboardColor = NamedTextColor.LIGHT_PURPLE,
    )

    val AQUA: GameTeam = GameTeam(
        id = "aqua",
        name = mm("Aqua Team"),
        prefix = mm("<aqua><bold>M"),
        formatTag = "<aqua>",
        color = GTColor.AQUA,
        scoreboardColor = NamedTextColor.AQUA,
    )

    fun addAll() {
        try {
            teamService.createTeam(GAME_MASTER, suppressEvent = true)
            teamService.createTeam(CASTER, suppressEvent = true)
            teamService.createTeam(SPECTATOR, suppressEvent = true)
            teamService.createTeam(BLUE, suppressEvent = true)
            teamService.createTeam(RED, suppressEvent = true)
            teamService.createTeam(GREEN, suppressEvent = true)
            teamService.createTeam(YELLOW, suppressEvent = true)
            teamService.createTeam(MAGENTA, suppressEvent = true)
            teamService.createTeam(AQUA, suppressEvent = true)
        } catch (exception: TeamCreationException) {
            logger.log(Level.SEVERE, exception) { "Could not create default teams." }
        }
    }
}
