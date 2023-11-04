package io.zkz.mc.gametools.util

fun Int.ordinal(): String {
    val suffixes = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")
    return when (this % 100) {
        11, 12, 13 -> this.toString() + "th"
        else -> this.toString() + suffixes[this % 10]
    }
}
