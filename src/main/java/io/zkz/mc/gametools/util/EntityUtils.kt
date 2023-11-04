package io.zkz.mc.gametools.util

import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType.BLAZE
import org.bukkit.entity.EntityType.CAVE_SPIDER
import org.bukkit.entity.EntityType.CREEPER
import org.bukkit.entity.EntityType.DROWNED
import org.bukkit.entity.EntityType.ELDER_GUARDIAN
import org.bukkit.entity.EntityType.ENDERMAN
import org.bukkit.entity.EntityType.ENDERMITE
import org.bukkit.entity.EntityType.EVOKER
import org.bukkit.entity.EntityType.GHAST
import org.bukkit.entity.EntityType.GIANT
import org.bukkit.entity.EntityType.GUARDIAN
import org.bukkit.entity.EntityType.HOGLIN
import org.bukkit.entity.EntityType.HUSK
import org.bukkit.entity.EntityType.ILLUSIONER
import org.bukkit.entity.EntityType.MAGMA_CUBE
import org.bukkit.entity.EntityType.PHANTOM
import org.bukkit.entity.EntityType.PIGLIN
import org.bukkit.entity.EntityType.PIGLIN_BRUTE
import org.bukkit.entity.EntityType.PILLAGER
import org.bukkit.entity.EntityType.PUFFERFISH
import org.bukkit.entity.EntityType.RAVAGER
import org.bukkit.entity.EntityType.SHULKER
import org.bukkit.entity.EntityType.SILVERFISH
import org.bukkit.entity.EntityType.SKELETON
import org.bukkit.entity.EntityType.SKELETON_HORSE
import org.bukkit.entity.EntityType.SLIME
import org.bukkit.entity.EntityType.SPIDER
import org.bukkit.entity.EntityType.STRAY
import org.bukkit.entity.EntityType.VINDICATOR
import org.bukkit.entity.EntityType.WARDEN
import org.bukkit.entity.EntityType.WITCH
import org.bukkit.entity.EntityType.WITHER_SKELETON
import org.bukkit.entity.EntityType.ZOGLIN
import org.bukkit.entity.EntityType.ZOMBIE
import org.bukkit.entity.EntityType.ZOMBIE_HORSE
import org.bukkit.entity.EntityType.ZOMBIE_VILLAGER
import org.bukkit.entity.EntityType.ZOMBIFIED_PIGLIN

fun Entity.isHostile(): Boolean {
    return when (type) {
        ELDER_GUARDIAN, WITHER_SKELETON, STRAY, HUSK, ZOMBIE_VILLAGER, SKELETON_HORSE, ZOMBIE_HORSE, EVOKER, VINDICATOR, ILLUSIONER, CREEPER, SKELETON, SPIDER, GIANT, ZOMBIE, SLIME, GHAST, ZOMBIFIED_PIGLIN, ENDERMAN, CAVE_SPIDER, SILVERFISH, BLAZE, MAGMA_CUBE, WITCH, ENDERMITE, GUARDIAN, SHULKER, PHANTOM, PUFFERFISH, DROWNED, PILLAGER, RAVAGER, HOGLIN, PIGLIN, WARDEN, PIGLIN_BRUTE, ZOGLIN -> true

        else -> false
    }
}
