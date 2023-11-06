package io.zkz.mc.gametools.settings

import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.settings.menu.GameSettingsMenu
import org.bukkit.entity.Player

@Injectable
class GameSettingsService(plugin: GameToolsPlugin) : PluginService<GameToolsPlugin>(plugin) {
    private val _settings: MutableMap<GameSettingCategory, MutableList<IGameSetting<*>>> = HashMap()

    fun registerSetting(category: GameSettingCategory, setting: IGameSetting<*>) {
        if (!_settings.containsKey(category)) {
            _settings[category] = ArrayList()
        }

        _settings[category]!!.add(setting)
    }

    fun openMenu(player: Player) {
        // TODO: permissions & categories
        GameSettingsMenu.create(withCategories = true, canEdit = true)
            .open(player)
    }

    val categories: Set<GameSettingCategory>
        get() = _settings.keys

    val settings: Map<GameSettingCategory, List<IGameSetting<*>>>
        get() = _settings

    override fun onEnable() {
//        var cat = new GameSettingCategory(
//            mm("Test Category"),
//            mm("Test Description"),
//            ISB.stack(Material.IRON_PICKAXE)
//        );
//
//        this.registerSetting(cat, new BooleanSetting(
//            mm("Test Setting 1"),
//            mm("Test Description 1"),
//            ISB.stack(Material.DIAMOND_AXE),
//            () -> true
//        ));
//
//        this.registerSetting(cat, new BooleanSetting(
//            mm("Test Setting 2"),
//            mm("Test Description 2"),
//            ISB.stack(Material.DIAMOND_AXE),
//            () -> false
//        ));
//
//        for (int i = 0; i < 10; i++) {
//            this.registerSetting(cat, new BooleanSetting(
//                mm("Test Setting 3"),
//                mm("Test Description 3"),
//                ISB.stack(Material.GOLDEN_AXE),
//                () -> false
//            ));
//        }
//
//        var cat2 = new GameSettingCategory(
//            mm("Test Category 2"),
//            mm("Test Description 2"),
//            ISB.stack(Material.IRON_PICKAXE)
//        );
//
//        this.registerSetting(cat2, new BooleanSetting(
//            mm("Test Setting 4"),
//            mm("Test Description 4"),
//            ISB.stack(Material.IRON_AXE),
//            () -> false
//        ));
//
//
//        for (int i = 0; i < 10; i++) {
//            this.registerSetting(
//                new GameSettingCategory(
//                    mm("Test Category X" + i),
//                    mm("Test Description X" + i),
//                    ISB.stack(Material.RED_WOOL)
//                ),
//                new BooleanSetting(
//                    mm("Test Setting X" + i),
//                    mm("Test Description X" + i),
//                    ISB.stack(Material.BLUE_WOOL),
//                    () -> false
//                )
//            );
//        }
    }

    // TODO: settings types

    // TODO:  - long
    // TODO:  - double
    // TODO:  - float
    // TODO:  - string
}
