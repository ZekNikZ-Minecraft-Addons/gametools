package io.zkz.mc.gametools.settings.menu

import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.inventory.CustomUI
import io.zkz.mc.gametools.inventory.Pagination
import io.zkz.mc.gametools.inventory.PaginationIterator
import io.zkz.mc.gametools.inventory.SlotIterator
import io.zkz.mc.gametools.inventory.UIContents
import io.zkz.mc.gametools.inventory.item.ClickableItem
import io.zkz.mc.gametools.settings.GameSettingCategory
import io.zkz.mc.gametools.settings.GameSettingsService
import io.zkz.mc.gametools.settings.IGameSetting
import io.zkz.mc.gametools.util.ISB
import io.zkz.mc.gametools.util.mm
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.math.ceil

class GameSettingsMenu(
    inv: CustomUI,
    player: Player,
    private val withCategories: Boolean,
    private val canEdit: Boolean,
) : UIContents(inv, player) {
    private lateinit var categoryPager: Pagination
    private lateinit var settingPager: Pagination
    private var settingDisplayIterator: PaginationIterator? = null
    private var settingValueIterator: PaginationIterator? = null
    private var base = 0

    private val gameSettingsService by inject<GameSettingsService>()

    override fun init() {
        base = if (withCategories) 3 else 1

        val categories: List<GameSettingCategory> = gameSettingsService.categories
            .sortedBy { PlainTextComponentSerializer.plainText().serialize(it.name).lowercase(Locale.getDefault()) }

        // Categories
        if (withCategories) {
            categoryPager = this.createPagination({ prevPage: Int, newPage: Int -> updateCategoryButtons(prevPage, newPage) }, ceil(categories.size.toDouble() / 7).toInt())
            val categoryIter = this.createIterator(SlotIterator.Type.HORIZONTAL, 1, 1)
            categoryPager.createIterator(
                categoryIter,
                7,
                categories
                    .map { category: GameSettingCategory ->
                        ClickableItem.of(
                            ISB.fromStack(category.displayIcon) {
                                name(category.name)
                                lore(category.description)
                                addItemFlags(
                                    ItemFlag.HIDE_ENCHANTS,
                                    ItemFlag.HIDE_ATTRIBUTES,
                                    ItemFlag.HIDE_UNBREAKABLE,
                                    ItemFlag.HIDE_DESTROYS,
                                    ItemFlag.HIDE_PLACED_ON,
                                    ItemFlag.HIDE_ITEM_SPECIFICS,
                                    ItemFlag.HIDE_DYE,
                                )
                            },
                        ) { setCategory(category) }
                    },
            )
        }

        // Settings
        settingPager = this.createPagination({ prevPage: Int, newPage: Int -> updateSettings(prevPage, newPage) }, 0)
        setCategory(categories[0])
    }

    private fun updateCategoryButtons(@Suppress("UNUSED_PARAMETER") prevPage: Int, newPage: Int) {
        // Left button
        if (newPage > 0) {
            this[1, 0] = ClickableItem.of(ItemStack(Material.ARROW)) { categoryPager.prev() }
        } else {
            this[1, 0] = ClickableItem.of(ItemStack(Material.AIR))
        }

        // Right button
        if (newPage < categoryPager.numPages - 1) {
            this[1, 8] = ClickableItem.of(ItemStack(Material.ARROW)) { categoryPager.next() }
        } else {
            this[1, 8] = ClickableItem.of(ItemStack(Material.AIR))
        }
    }

    private fun updateSettings(@Suppress("UNUSED_PARAMETER") prevPage: Int, newPage: Int) {
        // Left button
        if (newPage > 0) {
            this[base + 2, 0] = ClickableItem.of(ItemStack(Material.ARROW)) { settingPager.prev() }
        } else {
            this[base + 2, 0] = ClickableItem.of(ItemStack(Material.AIR))
        }

        // Right button
        if (newPage < settingPager.numPages - 1) {
            this[base + 2, 8] = ClickableItem.of(ItemStack(Material.ARROW)) { settingPager.next() }
        } else {
            this[base + 2, 8] = ClickableItem.of(ItemStack(Material.AIR))
        }
    }

    private fun setCategory(category: GameSettingCategory) {
        if (settingDisplayIterator != null) {
            settingPager.removeIterator(settingDisplayIterator!!)
            settingPager.removeIterator(settingValueIterator!!)
        }

        val settings: List<IGameSetting<*>> = gameSettingsService.settings[category]!!

        settingPager.numPages = ceil(settings.size.toDouble() / 7).toInt()

        val displayIter = this.createIterator(SlotIterator.Type.HORIZONTAL, base, 1)
        settingDisplayIterator = settingPager.createIterator(
            displayIter,
            7,
            settings
                .map { setting ->
                    ClickableItem.of(
                        ISB.fromStack(setting.displayIcon) {
                            name(setting.name)
                            if (setting.description != null) {
                                lore(setting.description!!)
                            }
                            addItemFlags(
                                ItemFlag.HIDE_ENCHANTS,
                                ItemFlag.HIDE_ATTRIBUTES,
                                ItemFlag.HIDE_UNBREAKABLE,
                                ItemFlag.HIDE_DESTROYS,
                                ItemFlag.HIDE_PLACED_ON,
                                ItemFlag.HIDE_ITEM_SPECIFICS,
                                ItemFlag.HIDE_DYE,
                            )
                        },
                    )
                },
        )

        val valueIter = this.createIterator(SlotIterator.Type.HORIZONTAL, base + 1, 1)
        settingValueIterator = settingPager.createIterator(
            valueIter,
            settings
                .map { setting ->
                    {
                        ClickableItem.of(
                            setting.optionIcon,
                        ) { clickType ->
                            if (!canEdit) {
                                return@of
                            }

                            setting.handleClick(clickType)
                            settingPager.page = (settingPager.page) // reset page
                        }
                    }
                },
            7,
        )
    }

    companion object {
        fun create(withCategories: Boolean, canEdit: Boolean): CustomUI {
            return CustomUI(
                "settings",
                mm("Game Settings"),
                if (withCategories) 6 else 4,
                9,
            ) { inv, player -> GameSettingsMenu(inv, player, withCategories, canEdit) }
        }
    }
}
