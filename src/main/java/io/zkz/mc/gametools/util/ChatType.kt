package io.zkz.mc.gametools.util

import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder

enum class ChatType(
    private val withoutPointsFormat: String,
    private val withPointsFormat: String?,
    private val withErrorFormat: String? = null
) : InjectionComponent {
    NORMAL(
        "<message>",
        Constants.POINT_PREFIX + "<message>"
    ),
    ALERT(
        Constants.INFO_PREFIX + "<legacy_aqua><b><message>",
        Constants.POINT_PREFIX + Constants.INFO_PREFIX + "<legacy_aqua><b><message>"
    ),
    WARNING(
        Constants.INFO_PREFIX + "<legacy_dark_red><b><message>",
        Constants.POINT_PREFIX + Constants.INFO_PREFIX + "<legacy_dark_red><b><message>"
    ),
    PASSIVE_INFO(
        Constants.INFO_PREFIX + "<gray><message>",
        Constants.POINT_PREFIX + "<gray><message>"
    ),
    ACTIVE_INFO(
        Constants.INFO_PREFIX + "<message>",
        Constants.POINT_PREFIX + "<message>"
    ),
    SUCCESS(
        Constants.INFO_PREFIX + "<legacy_green><b><message>",
        Constants.POINT_PREFIX + "<legacy_green><b><message>"
    ),
    ELIMINATION(
        "<gray>[<legacy_red>\u2620<gray>] <message>",
        Constants.POINT_PREFIX + "<gray>[<legacy_red>\u2620<gray>]<reset> <message>"
    ),
    TEAM_ELIMINATION(
        "[<legacy_red>\u2620\u2620\u2620<reset>] <message>",
        Constants.POINT_PREFIX + "[<legacy_red>\u2620\u2620\u2620<reset>] <message>"
    ),
    GAME_INFO(
        Constants.GAME_PREFIX + "<message>",
        Constants.POINT_PREFIX + Constants.GAME_PREFIX + "<message>"
    ),
    GAME_SUCCESS(
        Constants.GAME_PREFIX + "<legacy_green><message>",
        Constants.POINT_PREFIX + Constants.GAME_PREFIX + "<legacy_green><message>"
    ),
    COMMAND_SUCCESS(
        "<gray><message>",
        null
    ),
    COMMAND_ERROR(
        "<legacy_red>Command error: <message>",
        null,
        "<legacy_red>Command error: <message>\n<dark_gray><cause>"
    );

    val constants by inject<GTConstants>()

    object Constants {
        const val INFO_CHAR = "<yellow>\u25B6</yellow>"
        const val POINT_CHAR = "<yellow>\u2605</yellow>"
        const val INFO_PREFIX = "[$INFO_CHAR] "
        const val POINT_PREFIX = "[+<points>$POINT_CHAR] "
        const val GAME_PREFIX = "[<gold><b><name><reset>] "
    }

    fun format(message: Component): Component {
        return this.format(withoutPointsFormat, message)
    }

    fun format(message: Component, points: Double): Component {
        if (withPointsFormat == null) {
            throw UnsupportedOperationException("Formatting with points is not supported for message type $name")
        }
        return this.format(withPointsFormat, message, points)
    }

    fun format(message: Component, cause: Throwable): Component {
        if (withErrorFormat == null) {
            throw UnsupportedOperationException("Formatting with cause is not supported for message type $name")
        }
        return this.format(withErrorFormat, message, cause)
    }

    private fun format(format: String, message: Component): Component {
        return mmResolve(
            format,
            Placeholder.component("message", message),
            Placeholder.unparsed("name", constants.gameName)
        )
    }

    private fun format(format: String?, message: Component, points: Double): Component {
        return mmResolve(
            format!!,
            Placeholder.component("message", message),
            Placeholder.unparsed("name", constants.gameName),
            Placeholder.unparsed("points", points.toString())
        )
    }

    private fun format(format: String?, message: Component, cause: Throwable): Component {
        return mmResolve(
            format!!,
            Placeholder.component("message", message),
            Placeholder.unparsed("name", constants.gameName),
            Placeholder.unparsed("cause", cause.message!!)
        )
    }
}