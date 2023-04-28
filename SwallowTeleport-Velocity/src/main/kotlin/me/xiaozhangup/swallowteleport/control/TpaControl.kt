package me.xiaozhangup.swallowteleport.control

import com.velocitypowered.api.proxy.Player
import me.xiaozhangup.swallowteleport.SwallowTeleport
import me.xiaozhangup.swallowteleport.SwallowTeleport.control
import me.xiaozhangup.swallowteleport.SwallowTeleport.teleport
import me.xiaozhangup.swallowteleport.opj.TeleportType
import java.util.concurrent.TimeUnit

object TpaControl {

    private val ACCEPT =
        SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#76b583>已同意请求</color>")
    private val DENY =
        SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#f07f5e>已拒绝请求</color>")

    fun Player.makeTpaRequest(to: Player) {
        //请求过了就别再发了
        if (control.has(this, to, TeleportType.TPA)) {
            this.sendMessage(SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#e0edfa>你已经发给 ${to.username} 传送请求了 <color:#f07f5e><click:run_command:'/tpacancel ${to.username}'>[取消]</click></color>"))
            return
        }

        //发送消息
        to.sendMessage(SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#e0edfa>$username 请求传送到你这里 <color:#76b583><click:run_command:'/tpayes $username'>[同意]</click></color> <color:#f07f5e><click:run_command:'/tpano $username'>[拒绝]</click></color></color>"))
        sendMessage(SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#e0edfa>传送请求已发送给 ${to.username}! <color:#f07f5e><click:run_command:'/tpacancel ${to.username}'>[取消]</click></color>"))
        control.add(this, to, TeleportType.TPA)

        //超时检测
        SwallowTeleport.server.scheduler.buildTask(SwallowTeleport.plugin) {
            if (control.has(this, to, TeleportType.TPA)) {
                control.del(this, to, TeleportType.TPA)
                this.sendMessage(SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#e0edfa>你发给 ${to.username} 的请求已过期</color>"))
            }
        }.delay(1L, TimeUnit.MINUTES).schedule()
    }

    fun Player.acceptTpa(from: Player) {
        if (control.has(from, this, TeleportType.TPA)) {
            this.sendMessage(ACCEPT)
            from.sendMessage(SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#76b583>${this.username} 同意了你的传送请求,传送中...</color>"))
            from.teleport(this)
            control.del(from, this, TeleportType.TPA)
        }
    }

    fun Player.denyTpa(from: Player) {
        if (control.has(from, this, TeleportType.TPA)) {
            this.sendMessage(DENY)
            from.sendMessage(SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#f07f5e>${this.username} 拒绝了你的传送请求</color>"))
            control.del(from, this, TeleportType.TPA)
        }
    }

    fun Player.cancelTpa(to: Player) {
        if (control.has(this, to, TeleportType.TPA)) {
            to.sendMessage(SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#e0edfa>${this.username} 取消了刚刚的请求</color>"))
            this.sendMessage(SwallowTeleport.miniMessage.deserialize("${SwallowTeleport.prefix} <color:#e0edfa>发送给 ${to.username} 的请求已取消</color>"))
            control.del(this, to, TeleportType.TPA)
        }
    }

}