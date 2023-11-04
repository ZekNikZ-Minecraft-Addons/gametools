package io.zkz.mc.gametools.proxy

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedWatchableObject
import io.zkz.mc.gametools.injection.InjectionComponent
import io.zkz.mc.gametools.injection.inject
import io.zkz.mc.gametools.teams.GameTeam
import io.zkz.mc.gametools.teams.TeamService
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

object ProtocolLibProxy : InjectionComponent {
    val teamService by inject<TeamService>()

    fun setupGlowing(plugin: Plugin?) {
        val protocolManager: ProtocolManager = ProtocolLibrary.getProtocolManager()

        protocolManager.addPacketListener(object : PacketAdapter(plugin, PacketType.Play.Server.ENTITY_METADATA) {
            override fun onPacketSending(event: PacketEvent) {
                if (!teamService.glowingEnabled) {
                    return
                }
                val packet: PacketContainer = event.packet.deepClone()
                val reciever: Player = event.player
                val receiverId: Int = reciever.entityId
                val recieverTeam: GameTeam? = teamService.getTeamOfPlayer(reciever)
                val packetAboutId: Int = packet.integers.read(0)
                val aboutPlayer: Player? = getPlayer(packetAboutId)
                if (receiverId == packetAboutId || aboutPlayer == null || aboutPlayer.gameMode == GameMode.SPECTATOR) {
                    return
                }
                val aboutPlayerTeam: GameTeam? = teamService.getTeamOfPlayer(aboutPlayer)
                if (aboutPlayerTeam == recieverTeam) {
                    val watchableObjectList: List<WrappedWatchableObject> =
                        packet.watchableCollectionModifier.read(0)
                    for (metadata in watchableObjectList) {
                        if (metadata.index == 0) {
                            var b = metadata.value as Byte
                            b = (b.toInt() or 64).toByte()
                            metadata.value = b
                        }
                    }
                }
                event.packet = packet
            }
        })
    }

    private fun getPlayer(entityId: Int): Player? {
        return Bukkit.getServer().onlinePlayers.firstOrNull { it.entityId == entityId }
    }
}
