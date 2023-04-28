package me.xiaozhangup.swallowteleport

import me.xiaozhangup.swallowteleport.SwallowTeleport.cache
import me.xiaozhangup.swallowteleport.SwallowTeleport.gson
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener

object MessagerListener : PluginMessageListener {

    override fun onPluginMessageReceived(p0: String, p1: Player, p2: ByteArray) {
        if ("swallow:request" == p0) {
            val requestMessage = gson.fromJson(String(p2), RequestMessage::class.java)
            Bukkit.getPlayer(requestMessage.to)?.let { to ->
                val from = Bukkit.getPlayer(requestMessage.from)
                from?.teleport(to) ?: cache.add(requestMessage)
            }
        }
    }
}