package me.xiaozhangup.swallowteleport.command

import com.velocitypowered.api.proxy.Player
import me.xiaozhangup.swallowteleport.ControlCenter.accept
import me.xiaozhangup.swallowteleport.ControlCenter.cancel
import me.xiaozhangup.swallowteleport.ControlCenter.deny
import me.xiaozhangup.swallowteleport.ControlCenter.makeRequest
import me.xiaozhangup.swallowteleport.SwallowTeleport.miniMessage
import me.xiaozhangup.swallowteleport.SwallowTeleport.prefix
import me.xiaozhangup.swallowteleport.SwallowTeleport.server
import net.kyori.adventure.text.Component
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.SkipTo
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.command.player
import taboolib.common.platform.function.submitAsync

@SkipTo(LifeCycle.ENABLE)
object CommandTpa {

    private val NO_PLAYER = miniMessage.deserialize("$prefix <color:#e0edfa>并没有找到这位玩家!</color>")
    private val NO_SELF = miniMessage.deserialize("$prefix <color:#e0edfa>你不能自己传送到自己!</color>")

    @Awake(LifeCycle.ENABLE)
    fun regTpa() {
        command("tpa", permissionDefault = PermissionDefault.TRUE) {
            dynamic(optional = true) {
                suggestion<ProxyPlayer>(uncheck = true) { _, _ ->
                    server.allPlayers.map { it.username }
                }
                execute<Player> {from, _, argument ->
                    submitAsync {
                        val to = server.getPlayer(argument)

                        if (to.isPresent) {
                            if (to.get() == from) {
                                from.sendMessage(NO_SELF)
                            } else {
                                from.makeRequest(to.get())
                            }
                        } else {
                            from.sendMessage(NO_PLAYER)
                        }
                    }
                }
            }

            execute<Player> { sender, _, _ ->
                submitAsync {
                    sender.sendMessage(miniMessage.deserialize("<br><br><br>"))
                    sender.sendMessage(miniMessage.deserialize(" <color:#a1caf1><b>玩家传送功能使用方法:</b></color><newline>"))
                    sender.sendMessage(miniMessage.deserialize("  <white>/tpa [玩家名]<white> <dark_gray>-</dark_gray> <gray>向某玩家发出传送请求</gray>"))
                    sender.sendMessage(miniMessage.deserialize("  <white>/tpyes [玩家名]<white> <dark_gray>-</dark_gray> <gray>同意某人的请求,通常不必手动输入</gray>"))
                    sender.sendMessage(miniMessage.deserialize("  <white>/tpno [玩家名]<white> <dark_gray>-</dark_gray> <gray>拒绝某人的请求,通常不必手动输入</gray>"))
                    sender.sendMessage(miniMessage.deserialize("<br>"))
                }
            }
        }

        command("tpyes", aliases = listOf("tpaccept"), permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.accept(it)
                        }
                    }
                }
            }
        }

        command("tpno", aliases = listOf("tpdeny"), permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.deny(it)
                        }
                    }
                }
            }
        }

        command("tpcancel", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> {sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.cancel(it)
                        }
                    }
                }
            }
        }
    }
}