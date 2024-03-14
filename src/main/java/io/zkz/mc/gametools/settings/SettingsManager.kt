package io.zkz.mc.gametools.settings

import io.zkz.mc.gametools.GTPlugin
import io.zkz.mc.gametools.data.IDataManager
import io.zkz.mc.gametools.data.IManagesData
import io.zkz.mc.gametools.data.JSONDataManager
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.util.ComponentUtils.toPlainTextString
import io.zkz.mc.gametools.util.observable.IObserver
import kotlin.io.path.Path

abstract class SettingsManager<P : GTPlugin<P>>(plugin: P) : PluginService<P>(plugin), IManagesData {
    private val gameSettingsService by inject<GameSettingsService>()

    private val settings = mutableMapOf<Pair<String, String>, IGameSetting<*>>()

    override val dataManagers: List<IDataManager>
        get() = listOf(
            JSONDataManager(
                Path("settings.json"),
                SettingsConfig::class,
                ::onLoad,
                ::onSave,
            ),
        )

    protected fun <T : Any, S : IGameSetting<T>> setting(
        category: GameSettingCategory,
        setting: S,
        observer: IObserver<IGameSetting<T>>? = null,
    ): S {
        gameSettingsService.registerSetting(category, setting)
        settings[category.name.toPlainTextString() to setting.name.toPlainTextString()] = setting
        observer?.let { setting.addListener(it) }
        return setting
    }

    private fun onLoad(config: SettingsConfig) {
        config.settings.forEach { (category, settingsInCategory) ->
            settingsInCategory.forEach { (setting, value) ->
                settings[category to setting]?.setFromJson(value)
            }
        }
    }

    private fun onSave(): SettingsConfig {
        return SettingsConfig(
            settings
                .map { (k, v) -> Triple(k.first, k.second, v) }
                .groupBy { it.first }
                .mapValues { it.value.associate { (_, name, setting) -> name to setting.valueAsJson } },
        )
    }

    companion object {
        data class SettingsConfig(val settings: Map<String, Map<String, Any?>>)
    }
}