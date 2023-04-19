package me.xiaozhangup.swallowteleport

import com.google.gson.Gson
import com.velocitypowered.api.proxy.Player
import com.velocitypowered.api.proxy.ProxyServer
import com.velocitypowered.api.proxy.messages.ChannelIdentifier
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier
import net.kyori.adventure.text.minimessage.MiniMessage
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.platform.VelocityPlugin

@RuntimeDependencies(
    RuntimeDependency(value = "net.kyori:adventure-text-minimessage:4.12.0")
)
object SwallowTeleport : Plugin() {

    lateinit var plugin: VelocityPlugin
        private set
    lateinit var server: ProxyServer
        private set
    val miniMessage: MiniMessage by lazy { MiniMessage.miniMessage() }
    val gson: Gson by lazy { Gson() }
    const val prefix: String = "<dark_gray>[<color:#a1caf1>传送</color>]</dark_gray>"
    val swallowRequest: ChannelIdentifier = MinecraftChannelIdentifier.from("swallow:request")

    override fun onEnable() {
        plugin = VelocityPlugin.getInstance()
        server = plugin.server

        plugin.server.channelRegistrar.register(swallowRequest)
    }

    //在被请求(to) 的玩家执行!
    fun Player.orderTeleport(from: Player) {
        sendPluginMessage(swallowRequest, RequestMessage(from.username, this.username).toJson().toByteArray())
    }
}