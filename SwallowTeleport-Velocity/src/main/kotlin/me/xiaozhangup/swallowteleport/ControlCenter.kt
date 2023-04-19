package me.xiaozhangup.swallowteleport

import com.velocitypowered.api.proxy.Player
import me.xiaozhangup.swallowteleport.ControlCenter.deny
import me.xiaozhangup.swallowteleport.SwallowTeleport.miniMessage
import me.xiaozhangup.swallowteleport.SwallowTeleport.orderTeleport
import me.xiaozhangup.swallowteleport.SwallowTeleport.plugin
import me.xiaozhangup.swallowteleport.SwallowTeleport.prefix
import me.xiaozhangup.swallowteleport.SwallowTeleport.server
import java.util.concurrent.TimeUnit

object ControlCenter {

    private val quest: MutableList<RequestMessage> = mutableListOf()
    private val ACCEPT = miniMessage.deserialize("$prefix <color:#76b583>已同意请求</color>")
    private val DENY = miniMessage.deserialize("$prefix <color:#f07f5e>已拒绝请求</color>")
    fun Player.makeRequest(to: Player) {
        //请求过了就别再发了
        if (has(this, to)) {
            this.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>你已经发给 ${to.username} 传送请求了 <color:#f07f5e><click:run_command:'/tpcancel ${to.username}'>[取消]</click></color>"))
            return
        }

        //发送消息
        to.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>$username 请求传送到你这里 <color:#76b583><click:run_command:'/tpyes $username'>[同意]</click></color> <color:#f07f5e><click:run_command:'/tpno $username'>[拒绝]</click></color></color>"))
        sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>传送请求已发送给 ${to.username}! <color:#f07f5e><click:run_command:'/tpcancel ${to.username}'>[取消]</click></color>"))
        add(this, to)

        //超时检测
        server.scheduler.buildTask(plugin) {
            if (has(this, to)) {
                del(this, to)
                this.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>你发给 ${to.username} 的请求已过期</color>"))
            }
        }.delay(1L, TimeUnit.MINUTES).schedule()
    }

    fun Player.accept(from: Player) {
        if (has(from, this)) {
            this.sendMessage(ACCEPT)
            from.sendMessage(miniMessage.deserialize("$prefix <color:#76b583>${this.username} 同意了你的传送请求,传送中...</color>"))
            this.orderTeleport(from)
            del(from, this)

            //无论如何都发给后端请求，后端缓存或者直接执行
            //Velocity多少执行一次
            this.currentServer.ifPresent() { server ->
                from.currentServer.ifPresent { fs ->
                    if (fs != server) {
                        val connectionRequestBuilder = from.createConnectionRequest(server.server)
                        connectionRequestBuilder.connect()
                    }
                }
            }
        }
    }

    fun Player.deny(from: Player) {
        if (has(from, this)) {
            this.sendMessage(DENY)
            from.sendMessage(miniMessage.deserialize("$prefix <color:#f07f5e>${this.username} 拒绝了你的传送请求</color>"))
            del(from, this)
        }
    }

    fun Player.cancel(to: Player) {
        if (has(this, to)) {
            to.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>${this.username} 取消了刚刚的请求</color>"))
            this.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>发送给 ${this.username} 的请求已取消</color>"))
            del(this, to)
        }
    }

    //针对List的便捷操作
    private fun has(from: Player, to: Player): Boolean {
        var boolean = false
        for (que in quest) {
            if (que.from == from.username && que.to == to.username) {
                boolean = true
                break
            }
        }
        return boolean
    }

    private fun add(from: Player, to: Player) {
        val que = RequestMessage(from.username, to.username)
        quest.add(que)
    }

    private fun del(from: Player, to: Player) {
        quest.removeIf { que ->
            que.from == from.username && que.to == to.username
        }
    }

}