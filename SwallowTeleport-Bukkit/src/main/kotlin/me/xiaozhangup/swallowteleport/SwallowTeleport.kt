package me.xiaozhangup.swallowteleport

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitPlugin

object SwallowTeleport : Plugin() {

    private val plugin by lazy { BukkitPlugin.getInstance() }
    val gson: Gson by lazy { Gson() }
    val cache: MutableList<RequestMessage> = mutableListOf()

    override fun onEnable() {
        plugin.server.messenger.registerIncomingPluginChannel(plugin, "swallow:request", MessagerListener)
    }

    @SubscribeEvent(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun e(e: PlayerJoinEvent) {
        for (que in cache) {
            if (que.from == e.player.name) {
                Bukkit.getPlayer(que.to)?.let {
                    e.player.teleportAsync(it.location.clone()).thenAccept {
                        e.player.freezeTeleport(10L)
                    }
                }
            }
        }
        cache.removeIf { que ->
            que.from == e.player.name
        }
    }
}