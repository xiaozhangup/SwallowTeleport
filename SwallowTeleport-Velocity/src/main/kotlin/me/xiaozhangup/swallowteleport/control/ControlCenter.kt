package me.xiaozhangup.swallowteleport.control

import com.velocitypowered.api.proxy.Player
import me.xiaozhangup.swallowteleport.opj.RequestTask
import me.xiaozhangup.swallowteleport.opj.TeleportType

object ControlCenter {

    private val quest: MutableList<RequestTask> = mutableListOf()

    //针对List的便捷操作
    fun has(from: Player, to: Player, type: TeleportType): Boolean {
        var boolean = false
        for (que in quest) {
            if (que.from == from.username && que.to == to.username && que.type == type) {
                boolean = true
                break
            }
        }
        return boolean
    }

    fun add(from: Player, to: Player, type: TeleportType) {
        val que = RequestTask(from.username, to.username, type)
        quest.add(que)
    }

    fun del(from: Player, to: Player, type: TeleportType) {
        quest.removeIf { que ->
            que.from == from.username && que.to == to.username && que.type == type
        }
    }

}