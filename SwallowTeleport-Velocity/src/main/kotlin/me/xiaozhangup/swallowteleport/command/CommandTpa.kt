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

@SkipTo(LifeCycle.ENABLE)
object CommandTpa {

    private val NO_PLAYER = miniMessage.deserialize("$prefix <color:#e0edfa>并没有找到这位玩家!</color>")
    private val NO_TYPE = miniMessage.deserialize("$prefix <color:#e0edfa>请输入你想传送到的玩家!</color>")

    @Awake(LifeCycle.ENABLE)
    fun regTpa() {
        command("tpa", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                suggestion<ProxyPlayer> { _, _ ->
                    server.allPlayers.map { it.username }
                }
                execute<Player> {from, _, argument ->
                    val to = server.getPlayer(argument)

                    to.ifPresent {
                        from.makeRequest(it)
                        return@ifPresent
                    }
                    from.sendMessage(NO_PLAYER)
                }
            }

            execute<Player> { sender, _, _ ->
                sender.sendMessage(NO_TYPE)
            }
        }

        command("tpyes", aliases = listOf("tpaccept"), permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    server.getPlayer(argument).ifPresent {
                        sender.accept(it)
                    }
                }
            }
        }

        command("tpno", aliases = listOf("tpdeny"), permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    server.getPlayer(argument).ifPresent {
                        sender.deny(it)
                    }
                }
            }
        }

        command("tpcancel", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> {sender, _, argument ->
                    server.getPlayer(argument).ifPresent {
                        sender.cancel(it)
                    }
                }
            }
        }
    }
}