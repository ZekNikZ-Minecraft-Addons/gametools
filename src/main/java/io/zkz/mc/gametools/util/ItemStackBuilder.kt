package io.zkz.mc.gametools.util

import net.kyori.adventure.text.Component
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.potion.PotionType

class ItemStackBuilder private constructor(stack: ItemStack) {
    private val stack: ItemStack
    private val lore: MutableList<Component> = mutableListOf()
    private var name: Component? = null
    private var unbreakable: Boolean? = null
    private var damage: Int? = null
    private val canPlaceOn: MutableList<Material> = mutableListOf()
    private val canBreak: MutableList<Material> = mutableListOf()

    init {
        this.stack = stack.clone()

        val prevMeta = stack.itemMeta
        prevMeta?.let {
            if (it.hasLore()) {
                lore.addAll(it.lore()!!)
            }

            // TODO: copy CanPlaceOn and CanBreak
        }
    }

    fun material(material: Material): ItemStackBuilder {
        stack.setType(material)
        return this
    }

    fun amount(count: Int): ItemStackBuilder {
        stack.amount = count
        return this
    }

    fun damage(damage: Int): ItemStackBuilder {
        this.damage = damage
        return this
    }

    fun meta(meta: ItemMeta?): ItemStackBuilder {
        stack.setItemMeta(meta)
        return this
    }

    fun meta(metaModifier: (ItemMeta) -> Unit): ItemStackBuilder {
        val meta: ItemMeta = stack.itemMeta
        metaModifier(meta)
        stack.setItemMeta(meta)
        return this
    }

    fun potion(potionType: PotionType): ItemStackBuilder {
        return this.meta { (it as PotionMeta).basePotionType = potionType }
    }

    fun name(name: Component): ItemStackBuilder {
        this.name = mm("<!i><0>", name)
        return this
    }

    fun lore(lore: Component): ItemStackBuilder {
        this.lore.add(mm("<!i><gray><0>", lore))
        return this
    }

    fun lore(lore: List<Component>): ItemStackBuilder {
        lore.forEach(this::lore)
        return this
    }

    fun lore(vararg lore: Component): ItemStackBuilder {
        return this.lore(listOf(*lore))
    }

    fun unbreakable(): ItemStackBuilder {
        unbreakable = true
        return this
    }

    fun unbreakable(unbreakable: Boolean): ItemStackBuilder {
        this.unbreakable = unbreakable
        return this
    }

    fun skullOwner(player: OfflinePlayer): ItemStackBuilder {
        val meta = stack.itemMeta as SkullMeta
        meta.setOwningPlayer(player)
        return this
    }

    @Suppress("DEPRECATION")
    fun skullOwner(name: String?): ItemStackBuilder {
        val meta = stack.itemMeta as SkullMeta
        meta.setOwner(name)
        return this
    }

    fun addEnchantment(enchantment: Enchantment, level: Int): ItemStackBuilder {
        stack.addEnchantment(enchantment, level)
        return this
    }

    fun addEnchantments(enchantments: Map<Enchantment, Int>): ItemStackBuilder {
        stack.addEnchantments(enchantments)
        return this
    }

    fun addUnsafeEnchantment(enchantment: Enchantment, level: Int): ItemStackBuilder {
        stack.addUnsafeEnchantment(enchantment, level)
        return this
    }

    fun addUnsafeEnchantments(enchantments: Map<Enchantment, Int>): ItemStackBuilder {
        stack.addUnsafeEnchantments(enchantments)
        return this
    }

    fun addItemFlags(vararg itemFlags: ItemFlag): ItemStackBuilder {
        stack.addItemFlags(*itemFlags)
        return this
    }

    fun canPlaceOn(vararg materials: Material): ItemStackBuilder {
        canPlaceOn.addAll(listOf(*materials))
        return this
    }

    fun canBreak(vararg materials: Material): ItemStackBuilder {
        canBreak.addAll(listOf(*materials))
        return this
    }

    fun build(): ItemStack {
        var result: ItemStack = stack.clone()
        val meta: ItemMeta = result.itemMeta
        if (name != null) {
            meta.displayName(name)
        }
        if (lore.isNotEmpty()) {
            meta.lore(lore)
        }
        if (unbreakable != null) {
            meta.isUnbreakable = unbreakable!!
        }
        if (damage != null) {
            (meta as Damageable).damage = damage!!
        }
        result.setItemMeta(meta)
        if (canPlaceOn.isNotEmpty() || canBreak.isNotEmpty()) {
            val nmsStack = CraftItemStack.asNMSCopy(result)
            val tag = nmsStack.getOrCreateTag()
            if (canPlaceOn.isNotEmpty()) {
                val canPlaceOnTag = ListTag()
                canPlaceOn.forEach { canPlaceOnTag.add(StringTag.valueOf(it.getKey().toString())) }
                tag.put("CanPlaceOn", canPlaceOnTag)
            }
            if (canBreak.isNotEmpty()) {
                val canBreakTag = ListTag()
                canBreak.forEach { canBreakTag.add(StringTag.valueOf(it.getKey().toString())) }
                tag.put("CanDestroy", canBreakTag)
            }
            nmsStack.setTag(tag)
            result = CraftItemStack.asBukkitCopy(nmsStack)
        }
        return result
    }

    companion object {
        fun builder(): ItemStackBuilder {
            return fromMaterial(Material.AIR)
        }

        fun fromMaterial(material: Material): ItemStackBuilder {
            return ItemStackBuilder(ItemStack(material))
        }

        fun fromStack(stack: ItemStack): ItemStackBuilder {
            return ItemStackBuilder(stack)
        }
    }
}
