package io.zkz.mc.gametools.util

import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material

object BlockUtils {
    private val WOOLS = setOf(
        Material.WHITE_WOOL,
        Material.BLACK_WOOL,
        Material.BLUE_WOOL,
        Material.BROWN_WOOL,
        Material.CYAN_WOOL,
        Material.GRAY_WOOL,
        Material.GREEN_WOOL,
        Material.LIGHT_BLUE_WOOL,
        Material.LIGHT_GRAY_WOOL,
        Material.LIME_WOOL,
        Material.MAGENTA_WOOL,
        Material.ORANGE_WOOL,
        Material.PINK_WOOL,
        Material.PURPLE_WOOL,
        Material.RED_WOOL,
        Material.YELLOW_WOOL
    )
    private val CONCRETES = setOf(
        Material.WHITE_CONCRETE,
        Material.BLACK_CONCRETE,
        Material.BLUE_CONCRETE,
        Material.BROWN_CONCRETE,
        Material.CYAN_CONCRETE,
        Material.GRAY_CONCRETE,
        Material.GREEN_CONCRETE,
        Material.LIGHT_BLUE_CONCRETE,
        Material.LIGHT_GRAY_CONCRETE,
        Material.LIME_CONCRETE,
        Material.MAGENTA_CONCRETE,
        Material.ORANGE_CONCRETE,
        Material.PINK_CONCRETE,
        Material.PURPLE_CONCRETE,
        Material.RED_CONCRETE,
        Material.YELLOW_CONCRETE
    )
    private val LOGS = setOf(
        Material.OAK_LOG,
        Material.DARK_OAK_LOG,
        Material.BIRCH_LOG,
        Material.ACACIA_LOG,
        Material.SPRUCE_LOG,
        Material.JUNGLE_LOG,
        Material.MANGROVE_LOG
    )
    private val LEAVES = setOf(
        Material.OAK_LEAVES,
        Material.DARK_OAK_LEAVES,
        Material.BIRCH_LEAVES,
        Material.ACACIA_LEAVES,
        Material.SPRUCE_LEAVES,
        Material.JUNGLE_LEAVES,
        Material.MANGROVE_LEAVES
    )

    val allWools: Set<Material>
        get() = WOOLS

    fun isWool(material: Material): Boolean {
        return WOOLS.contains(material)
    }

    fun getWoolColor(color: NamedTextColor): Material? {
        if (color.value() == NamedTextColor.BLACK.value()) {
            return Material.BLACK_WOOL
        } else if (color.value() == NamedTextColor.DARK_BLUE.value()) {
            return Material.BLUE_WOOL
        } else if (color.value() == NamedTextColor.DARK_GREEN.value()) {
            return Material.GREEN_WOOL
        } else if (color.value() == NamedTextColor.DARK_AQUA.value()) {
            return Material.CYAN_WOOL
        } else if (color.value() == NamedTextColor.DARK_RED.value()) {
            return Material.RED_WOOL
        } else if (color.value() == NamedTextColor.DARK_PURPLE.value()) {
            return Material.PURPLE_WOOL
        } else if (color.value() == NamedTextColor.GOLD.value()) {
            return Material.ORANGE_WOOL
        } else if (color.value() == NamedTextColor.GRAY.value()) {
            return Material.LIGHT_GRAY_WOOL
        } else if (color.value() == NamedTextColor.DARK_GRAY.value()) {
            return Material.GRAY_WOOL
        } else if (color.value() == NamedTextColor.BLUE.value()) {
            return Material.BLUE_WOOL
        } else if (color.value() == NamedTextColor.GREEN.value()) {
            return Material.LIME_WOOL
        } else if (color.value() == NamedTextColor.AQUA.value()) {
            return Material.LIGHT_BLUE_WOOL
        } else if (color.value() == NamedTextColor.RED.value()) {
            return Material.PINK_WOOL
        } else if (color.value() == NamedTextColor.LIGHT_PURPLE.value()) {
            return Material.MAGENTA_WOOL
        } else if (color.value() == NamedTextColor.YELLOW.value()) {
            return Material.YELLOW_WOOL
        } else if (color.value() == NamedTextColor.WHITE.value()) {
            return Material.WHITE_WOOL
        }
        return null
    }

    val allConcretes: Set<Material>
        get() = CONCRETES

    fun isConcrete(material: Material): Boolean {
        return CONCRETES.contains(material)
    }

    fun getConcreteColor(color: NamedTextColor): Material? {
        if (color.value() == NamedTextColor.BLACK.value()) {
            return Material.BLACK_CONCRETE
        } else if (color.value() == NamedTextColor.DARK_BLUE.value()) {
            return Material.BLUE_CONCRETE
        } else if (color.value() == NamedTextColor.DARK_GREEN.value()) {
            return Material.GREEN_CONCRETE
        } else if (color.value() == NamedTextColor.DARK_AQUA.value()) {
            return Material.CYAN_CONCRETE
        } else if (color.value() == NamedTextColor.DARK_RED.value()) {
            return Material.RED_CONCRETE
        } else if (color.value() == NamedTextColor.DARK_PURPLE.value()) {
            return Material.PURPLE_CONCRETE
        } else if (color.value() == NamedTextColor.GOLD.value()) {
            return Material.ORANGE_CONCRETE
        } else if (color.value() == NamedTextColor.GRAY.value()) {
            return Material.LIGHT_GRAY_CONCRETE
        } else if (color.value() == NamedTextColor.DARK_GRAY.value()) {
            return Material.GRAY_CONCRETE
        } else if (color.value() == NamedTextColor.BLUE.value()) {
            return Material.BLUE_CONCRETE
        } else if (color.value() == NamedTextColor.GREEN.value()) {
            return Material.LIME_CONCRETE
        } else if (color.value() == NamedTextColor.AQUA.value()) {
            return Material.LIGHT_BLUE_CONCRETE
        } else if (color.value() == NamedTextColor.RED.value()) {
            return Material.PINK_CONCRETE
        } else if (color.value() == NamedTextColor.LIGHT_PURPLE.value()) {
            return Material.MAGENTA_CONCRETE
        } else if (color.value() == NamedTextColor.YELLOW.value()) {
            return Material.YELLOW_CONCRETE
        } else if (color.value() == NamedTextColor.WHITE.value()) {
            return Material.WHITE_CONCRETE
        }
        return null
    }

    fun isLog(type: Material): Boolean {
        return LOGS.contains(type)
    }

    fun isLeaves(type: Material): Boolean {
        return LEAVES.contains(type)
    }
}