package io.zkz.mc.gametools.resourcepack

@JvmRecord
data class FontProvider(
    val type: String,
    val file: String? = null,
    val ascent: Int? = null,
    val height: Int? = null,
    val chars: List<String>? = null,
    val advances: Map<String, Double>? = null,
)

@JvmRecord
data class FontData(
    val providers: MutableList<FontProvider> = mutableListOf(),
)
