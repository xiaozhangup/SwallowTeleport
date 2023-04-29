package me.xiaozhangup.swallowteleport.command

import com.velocitypowered.api.proxy.Player
import me.xiaozhangup.swallowteleport.SwallowTeleport.miniMessage
import me.xiaozhangup.swallowteleport.SwallowTeleport.prefix
import me.xiaozhangup.swallowteleport.SwallowTeleport.server
import me.xiaozhangup.swallowteleport.SwallowTeleport.teleport
import me.xiaozhangup.swallowteleport.control.TpaControl.acceptTpa
import me.xiaozhangup.swallowteleport.control.TpaControl.cancelTpa
import me.xiaozhangup.swallowteleport.control.TpaControl.denyTpa
import me.xiaozhangup.swallowteleport.control.TpaControl.makeTpaRequest
import me.xiaozhangup.swallowteleport.control.TphControl.acceptTph
import me.xiaozhangup.swallowteleport.control.TphControl.cancelTph
import me.xiaozhangup.swallowteleport.control.TphControl.denyTph
import me.xiaozhangup.swallowteleport.control.TphControl.makeTphRequest
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.SkipTo
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.command
import taboolib.common.platform.function.submitAsync

@SkipTo(LifeCycle.ENABLE)
object CommandTeleport {

    private val NO_PLAYER = miniMessage.deserialize("$prefix <color:#e0edfa>并没有找到这位玩家!</color>")
    private val NO_SELF = miniMessage.deserialize("$prefix <color:#e0edfa>你不能自己传送到自己!</color>")

    @Awake(LifeCycle.ENABLE)
    fun regTpa() {
        //tpa part
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
                                from.makeTpaRequest(to.get())
                            }
                        } else {
                            from.sendMessage(NO_PLAYER)
                        }
                    }
                }
            }

            execute<Player> { sender, _, _ ->
                sender.sendHelp()
            }
        }
        command("tpayes", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.acceptTpa(it)
                        }
                    }
                }
            }
        }
        command("tpano", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.denyTpa(it)
                        }
                    }
                }
            }
        }
        command("tpacancel", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.cancelTpa(it)
                        }
                    }
                }
            }
        }

        // TODO: tph part
        command("tph", permissionDefault = PermissionDefault.TRUE) {
            dynamic(optional = true) {
                suggestion<ProxyPlayer>(uncheck = true) { _, _ ->
                    server.allPlayers.map { it.username }
                }
                execute<Player> { from, _, argument ->
                    submitAsync {
                        val to = server.getPlayer(argument)

                        if (to.isPresent) {
                            if (to.get() == from) {
                                from.sendMessage(NO_SELF)
                            } else {
                                from.makeTphRequest(to.get())
                            }
                        } else {
                            from.sendMessage(NO_PLAYER)
                        }
                    }
                }
            }

            execute<Player> { sender, _, _ ->
                sender.sendHelp()
            }
        }
        command("tphyes", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.acceptTph(it)
                        }
                    }
                }
            }
        }
        command("tphno", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.denyTph(it)
                        }
                    }
                }
            }
        }
        command("tphcancel", permissionDefault = PermissionDefault.TRUE) {
            dynamic {
                execute<Player> { sender, _, argument ->
                    submitAsync {
                        server.getPlayer(argument).ifPresent {
                            sender.cancelTph(it)
                        }
                    }
                }
            }
        }

        command("vtp", permissionDefault = PermissionDefault.FALSE) {
            dynamic(optional = true) {
                suggestion<ProxyPlayer>(uncheck = true) { _, _ ->
                    server.allPlayers.map { it.username }
                }
                execute<Player> { sender, _, argument ->
                    if (sender.hasPermission("swallowteleport.tp")) {
                        server.getPlayer(argument)?.let {
                            it.ifPresent { to ->
                                sender.teleport(to)
                            }
                        }
                    } else {
                        sender.sendHelp()
                    }
                }

                dynamic(optional = true) {
                    suggestion<ProxyPlayer>(uncheck = true) { _, _ ->
                        server.allPlayers.map { it.username }
                    }
                    execute<Player> { sender, com, to ->
                        if (sender.hasPermission("swallowteleport.tp")) {
                            server.getPlayer(to).ifPresent { tp ->
                                tp.sendMessage(miniMessage.deserialize("$prefix <color:#e0edfa>你被强制传送到 ${tp.username}!</color>"))
                                server.getPlayer(com.args()[0]).ifPresent { fp ->
                                    fp.teleport(tp)
                                }
                            }
                        } else {
                            sender.sendHelp()
                        }
                    }
                }
            }

            execute<Player> { sender, _, _ ->
                sender.sendHelp()
            }
        }
    }

    private fun Player.sendHelp() {
        submitAsync {
            sendMessage(miniMessage.deserialize("<br><br><br>"))
            sendMessage(miniMessage.deserialize(" <color:#a1caf1><b>玩家传送功能使用方法:</b></color><newline>"))
            sendMessage(miniMessage.deserialize("  <white>/tpa [玩家名]<white> <dark_gray>-</dark_gray> <gray>请求传送到某玩家</gray>"))
            sendMessage(miniMessage.deserialize("  <white>/tpayes [玩家名]<white> <dark_gray>-</dark_gray> <gray>同意某人的请求,通常不必手动输入</gray>"))
            sendMessage(miniMessage.deserialize("  <white>/tpano [玩家名]<white> <dark_gray>-</dark_gray> <gray>拒绝某人的请求,通常不必手动输入</gray><br>"))
            sendMessage(miniMessage.deserialize("  <white>/tph [玩家名]<white> <dark_gray>-</dark_gray> <gray>请求某玩家传送来</gray>"))
            sendMessage(miniMessage.deserialize("  <white>/tphyes [玩家名]<white> <dark_gray>-</dark_gray> <gray>同意某人的请求,通常不必手动输入</gray>"))
            sendMessage(miniMessage.deserialize("  <white>/tphno [玩家名]<white> <dark_gray>-</dark_gray> <gray>拒绝某人的请求,通常不必手动输入</gray>"))
            sendMessage(miniMessage.deserialize("<br>"))
        }
    }
}