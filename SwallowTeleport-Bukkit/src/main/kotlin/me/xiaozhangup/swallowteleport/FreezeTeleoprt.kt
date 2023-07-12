package me.xiaozhangup.swallowteleport

import me.xiaozhangup.swallowteleport.FreezeTeleoprt.teleportCooldown
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit

object FreezeTeleoprt {
    val teleportCooldown = mutableSetOf<Player>()

    @SubscribeEvent
    fun onPlayerTeleport(event: PlayerTeleportEvent) {
        val player = event.player
        if (player in teleportCooldown) {
            event.isCancelled = true
        }
    }
}

fun Player.freezeTeleport(delayTicks: Long) {
    teleportCooldown.add(this)
    submit(delay = delayTicks) { teleportCooldown.remove(this@freezeTeleport) }
}