package cn.mpsmc

import kotlinx.coroutines.launch
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.permission.PermitteeId.Companion.permitteeId
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info

object BotGPT : KotlinPlugin(
    JvmPluginDescription(
        id = "cn.mpsmc.botgpt",
        name = "BotGPT",
        version = "0.2.5",
    ) {
        author("sectly")
    }
) {


    override fun onEnable() {
        logger.info { "MPSMC BotGPT 加载中" }
        Config.reload()
        Data.reload()
        val chatPermission = PermissionService.INSTANCE.register(permissionId("botgpt.chat"), "BotGPT聊天权限")
        logger.info { " _____       _    _____  _____  _____ " }
        logger.info { "| __  | ___ | |_ |   __||  _  ||_   _|" }
        logger.info { "| __ -|| . ||  _||  |  ||   __|  | |  " }
        logger.info { "|_____||___||_|  |_____||__|     |_|  " }
        logger.info { "" }
        logger.info { "MPSMC BotGPT 已加载" }

            GlobalEventChannel.parentScope(this).subscribeAlways<GroupMessageEvent> { event ->
                    if (event.group.permitteeId.hasPermission(chatPermission) && (event.message[1] is At) && ((event.message[1] as At).target == event.bot.id) && event.message.size >= 3 && (event.message[2] is PlainText)) {
                        launch {
                            event.group.sendMessage(
                                chatEventHandler(
                                    event.group, event
                                )
                            )
                        }
                }
            }
            GlobalEventChannel.parentScope(this).subscribeAlways<FriendMessageEvent> { event ->
                run {
                    if (sender.permitteeId.hasPermission(chatPermission) && (event.message[1] is PlainText)) {
                        launch {
                            event.sender.sendMessage(
                                chatEventHandler(
                                    sender, event
                                )
                            )
                        }
                    }
                }
            }
    }
    override fun onDisable() {
        logger.info { "MPSMC BotGPT 已卸载" }
    }
}