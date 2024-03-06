package io.zkz.mc.gametools.resourcepack

import com.sun.net.httpserver.HttpServer
import io.zkz.mc.gametools.GameToolsPlugin
import io.zkz.mc.gametools.data.IDataManager
import io.zkz.mc.gametools.data.IManagesData
import io.zkz.mc.gametools.data.jsonSynced
import io.zkz.mc.gametools.injection.Injectable
import io.zkz.mc.gametools.service.PluginService
import io.zkz.mc.gametools.util.BukkitUtils.runNextTick
import io.zkz.mc.gametools.util.HashUtils.sha1Hash
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Executors
import kotlin.io.path.exists
import kotlin.io.path.inputStream

@Injectable
class ResourcePackService(
    plugin: GameToolsPlugin,
) : PluginService<GameToolsPlugin>(plugin), IManagesData {
    private val dataManager = jsonSynced<ResourcePackConfig>(plugin.dataFolder.toPath().resolve("resourcepack.json")) {
        ResourcePackConfig(null, null)
    }
    private val config by dataManager

    private var enabled = false
    private var packPath: Path? = null
    private var packSize: Long? = null
    private var packHash: String? = null
    private var httpServer: HttpServer? = null

    override fun onEnable() {
        runNextTick {
            this.rebuild()
        }
    }

    override fun onDisable() {
        httpServer?.stop(0)
        logger.info("Stopped HTTP server.")
    }

    private fun rebuild() {
        logger.info("Looking for resource pack providers...")

        val packProviders = injectionContainer.getAllOfType<IProvidesResourcePackParts>()

        if (packProviders.isEmpty()) {
            logger.info("No resource pack providers found.")
            return
        } else {
            logger.info("${packProviders.size} resource pack provider(s) found. Building resource pack...")
        }

        // Create output folder
        val outputPath = plugin.dataFolder.toPath().resolve("resource_pack_build")
        if (outputPath.exists()) {
            outputPath.toFile().deleteRecursively()
        }
        Files.createDirectories(outputPath)

        val resourcePackBuilder = ResourcePackBuilder(outputPath)

        packProviders.forEach {
            it.buildResourcePack(resourcePackBuilder)
        }
        if (resourcePackBuilder.dirty) {
            packPath = plugin.dataFolder.toPath().resolve("resource_pack.zip")
            resourcePackBuilder.build(packPath!!)
            logger.info("Resource pack built: $packPath")

            // Compute file size
            packSize = packPath!!.toFile().length()

            // Compute hash
            packHash = sha1Hash(packPath!!.toFile())
            logger.info("Resource pack hash: $packHash")

            enabled = true
            if (httpServer == null) {
                createHttpServer()
            }
        }
    }

    private fun createHttpServer() {
        httpServer = HttpServer.create(InetSocketAddress(0), 0).apply {
            createContext("/resource-pack") { exch ->
                exch.sendResponseHeaders(200, packSize!!)
                packPath!!.inputStream().buffered().use {
                    it.transferTo(exch.responseBody)
                }
            }
            executor = Executors.newCachedThreadPool()
            start()
        }
        logger.info("Started HTTP server on ${httpServer?.address}.")
    }

    @EventHandler
    private fun onPlayerJoin(event: PlayerJoinEvent) {
        if (enabled && packPath != null && packHash != null) {
            val address = config.httpHostAddress ?: discoverHttpHostAddress()

            event.player.setResourcePack(
                "http://$address:${httpServer!!.address.port}/resource-pack",
                packHash!!,
                true,
            )
        }
    }

    private fun discoverHttpHostAddress(): String {
        val addresses = NetworkInterface.getNetworkInterfaces().toList()
            .flatMap { ni ->
                ni.inetAddresses.toList().map { inet ->
                    inet.hostAddress
                }
            }

        // Find an external IP address
        for (address in addresses) {
            if (!address.startsWith("192.168") && !address.startsWith("169.254") && !address.startsWith("127.0.0")) {
                return address
            }
        }

        // Find a local address
        for (address in addresses) {
            if (address.startsWith("192.168")) {
                return address
            }
        }

        return "localhost"
    }

    override val dataManagers: List<IDataManager> = listOf(dataManager)
}
