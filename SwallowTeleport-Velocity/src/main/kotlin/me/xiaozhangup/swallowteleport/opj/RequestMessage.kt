package me.xiaozhangup.swallowteleport.opj

import me.xiaozhangup.swallowteleport.SwallowTeleport.gson

data class RequestMessage(val from: String, val to: String) {

    fun toJson(): String {
        return gson.toJson(this)
    }

}
