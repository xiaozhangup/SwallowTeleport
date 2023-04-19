package me.xiaozhangup.swallowteleport

import com.google.gson.Gson
import me.xiaozhangup.swallowteleport.MessagerListener.cache
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.platform.BukkitPlugin

object SwallowTeleport : Plugin() {

    lateinit var plugin: BukkitPlugin
        private set
    val gson: Gson by lazy { Gson() }

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