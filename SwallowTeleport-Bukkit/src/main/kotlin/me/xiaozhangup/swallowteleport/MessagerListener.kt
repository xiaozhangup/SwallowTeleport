package me.xiaozhangup.swallowteleport

import me.xiaozhangup.swallowteleport.SwallowTeleport.gson
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.messaging.PluginMessageListener
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submitAsync

object MessagerListener : PluginMessageListener {

    val cache: MutableList<RequestMessage> = mutableListOf()
    override fun onPluginMessageReceived(p0: String, p1: Player, p2: ByteArray) {
        submitAsync {
            if ("swallow:request" == p0) {
                val requestMessage = gson.fromJson(String(p2), RequestMessage::class.java)
                Bukkit.getPlayer(requestMessage.to)?.let { to ->
                    val from = Bukkit.getPlayer(requestMessage.from)
                    from?.teleport(to) ?: cache.add(requestMessage)
                }
            }
        }
    }
}