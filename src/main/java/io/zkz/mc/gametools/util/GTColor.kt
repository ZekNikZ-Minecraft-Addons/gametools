package io.zkz.mc.gametools.util

import net.kyori.adventure.text.format.TextColor
import org.bukkit.Color
import java.util.HashMap

data class GTColor(val rgb: Int) {
    val textColor = TextColor.color(rgb)
    val bukkit = Color.fromRGB(rgb)
    val awt = java.awt.Color(rgb)

    companion object {
        val COLORS: MutableMap<String, GTColor> = HashMap()

        private fun color(name: String, rgb: Int): GTColor? {
            COLORS[name] = GTColor(rgb)
            return COLORS[name]
        }

        val RED = color("red", 0xFB5455)
        val ORANGE = color("orange", 0xFCA800)
        val YELLOW = color("yellow", 0xFBFB00)
        val GREEN = color("green", 0x00A800)
        val LIME = color("lime", 0x54FB55)
        val BLUE = color("blue", 0x3B68F7)
        val AQUA = color("aqua", 0x42D7FC)
        val CYAN = color("cyan", 0x02A183)
        val MAGENTA = color("magenta", 0xFB54FB)
        val PURPLE = color("purple", 0x8632FC)
        val WHITE = color("white", 0xFFFFFF)
        val LIGHT_GRAY = color("light_gray", 0xA5ADAD)
        val DARK_GRAY = color("dark_gray", 0x545454)
        val BLACK = color("black", 0x2B2B2B)
        val ALERT_INFO = color("alert_info", 0x0AFFFF)
        val ALERT_SUCCESS = color("alert_success", 0x17FF32)
        val ALERT_ACCENT = color("alert_accent", 0xFFBB00)
        val ALERT_WARNING = color("alert_warning", 0xFC1B0F)

        val ALL: Set<GTColor>
            get() = COLORS.values.toSet()
    }
}
