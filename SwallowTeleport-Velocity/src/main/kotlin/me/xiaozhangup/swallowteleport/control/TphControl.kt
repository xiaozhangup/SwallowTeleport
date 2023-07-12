package me.xiaozhangup.swallowteleport.control

import com.velocitypowered.api.proxy.Player
import me.xiaozhangup.swallowteleport.SwallowTeleport.miniMessage
import me.xiaozhangup.swallowteleport.SwallowTeleport.plugin
import me.xiaozhangup.swallowteleport.SwallowTeleport.prefix
import me.xiaozhangup.swallowteleport.SwallowTeleport.server
import me.xiaozhangup.swallowteleport.SwallowTeleport.teleport
import me.xiaozhangup.swallowteleport.opj.TeleportType
import java.util.concurrent.TimeUnit

object TphControl {

    private val ACCEPT =
        miniMessage.deserialize("$prefix <color:#76b583>已同意请求,传送中...</color>")
    private val DENY =
        miniMessage.deserialize("$prefix <color:#f07f5e>已拒绝请求</color>")

    fun Player.makeTphRequest(to: Player) {
        //请求过了就别再发了
        if (ControlCenter.has(this, to, TeleportType.TPHERE)) {
            this.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>你已经发给 ${to.username} 传送请求了 <color:#f07f5e><click:run_command:'/tphcancel ${to.username}'>[取消]</click></color>"))
            return
        }

        //发送消息
        to.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>$username 请求你传送到他那里 <color:#76b583><click:run_command:'/tphyes $username'>[同意]</click></color> <color:#f07f5e><click:run_command:'/tphno $username'>[拒绝]</click></color></color>"))
        sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>传送请求已发送给 ${to.username}! <color:#f07f5e><click:run_command:'/tphcancel ${to.username}'>[取消]</click></color>"))
        ControlCenter.add(this, to, TeleportType.TPHERE)

        //超时检测
        server.scheduler.buildTask(plugin) {
            if (ControlCenter.has(this, to, TeleportType.TPHERE)) {
                ControlCenter.del(this, to, TeleportType.TPHERE)
                this.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>你发给 ${to.username} 的请求已过期</color>"))
                to.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>${this.username} 发给你的请求已过期</color>"))
            }
        }.delay(3L, TimeUnit.MINUTES).schedule()
    }

    fun Player.acceptTph(from: Player) {
        if (ControlCenter.has(from, this, TeleportType.TPHERE)) {
            this.sendMessage(ACCEPT)
            from.sendMessage(miniMessage.deserialize("$prefix <color:#76b583>${this.username} 同意了你的传送请求</color>"))
            this.teleport(from)
            ControlCenter.del(from, this, TeleportType.TPHERE)
        } else {
            this.sendMessage(miniMessage.deserialize("$prefix <color:#f07f5e>你没有待处理的传送请求</color>"))
        }
    }

    fun Player.denyTph(from: Player) {
        if (ControlCenter.has(from, this, TeleportType.TPHERE)) {
            this.sendMessage(DENY)
            from.sendMessage(miniMessage.deserialize("$prefix <color:#f07f5e>${this.username} 拒绝了你的传送请求</color>"))
            ControlCenter.del(from, this, TeleportType.TPHERE)
        } else {
            this.sendMessage(miniMessage.deserialize("$prefix <color:#f07f5e>你没有待处理的传送请求</color>"))
        }
    }

    fun Player.cancelTph(to: Player) {
        if (ControlCenter.has(this, to, TeleportType.TPHERE)) {
            to.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>${this.username} 取消了刚刚的请求</color>"))
            this.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>发送给 ${to.username} 的请求已取消</color>"))
            ControlCenter.del(this, to, TeleportType.TPHERE)
        } else {
            this.sendMessage(miniMessage.deserialize("$prefix <color:#f07f5e>你没有待处理的传送请求</color>"))
        }
    }

}