package me.xiaozhangup.swallowteleport

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.BukkitPlugin

object SwallowTeleport : Plugin() {

    lateinit var plugin: BukkitPlugin
        private set
    val gson: Gson by lazy { Gson() }
    val cache: MutableList<RequestMessage> = mutableListOf()

    override fun onEnable() {
        plugin = BukkitPlugin.getInstance()
        plugin.server.messenger.registerIncomingPluginChannel(plugin, "swallow:request", MessagerListener)
    }

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        for (que in cache) {
            if (que.from == e.player.name) {
                Bukkit.getPlayer(que.to)?.let {
                    e.player.teleport(it)
                }
            }
        }
        cache.removeIf { que ->
            que.from == e.player.name
        }
    }
}