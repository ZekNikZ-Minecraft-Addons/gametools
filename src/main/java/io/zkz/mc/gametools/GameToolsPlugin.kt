package io.zkz.mc.gametools

import io.zkz.mc.gametools.proxy.ProtocolLibProxy
import org.bukkit.plugin.PluginManager

class GameToolsPlugin : GTPlugin<GameToolsPlugin>() {
    override fun buildPluginDependentInjectables(pluginManager: PluginManager) {
//        if (pluginManager.getPlugin("WorldEdit") != null) {
//            logger.info("WorldEdit found, registering dependent services...")
//            WorldEditService.markAsLoaded()
//            this.register(WorldEditService.getInstance())
//            SchematicService.markAsLoaded()
//            this.register(SchematicService.getInstance())
//        }

//        if (pluginManager.getPlugin("WorldGuard") != null) {
//            logger.info("WorldGuard found, registering dependent services...")
//            RegionService.markAsLoaded()
//            this.register(RegionService.getInstance())
//        }

        if (pluginManager.getPlugin("ProtocolLib") != null) {
            ProtocolLibProxy.setupGlowing(this)
        }
    }
}