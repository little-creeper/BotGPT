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
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.utils.info

object BotGPT : KotlinPlugin(
    JvmPluginDescription(
        id = "cn.mpsmc.botgpt",
        name = "BotGPT",
        version = "0.1.0",
    ) {
        author("sectly")
    }
) {


    override fun onEnable() {
        Config.reload()
        Data.reload()
        Client.load()
        logger.info { "MPSMC BotGPT loaded" }
        val chatPermission = PermissionService.INSTANCE.register(permissionId("botgpt.chat"), "BotGPT聊天权限")
        launch {
            GlobalEventChannel.parentScope(this).subscribeAlways<GroupMessageEvent> { event ->
                run {
                    if (event.group.permitteeId.hasPermission(chatPermission) && (event.message[1] is At) && ((event.message[1] as At).target == event.bot.id) && (event.message[2] is PlainText)) {
                        event.group.sendMessage(
                            chatEventHandler(
                                sender.id,
                                event.message.contentToString().removeRange(0 until event.bot.id.toString().length + 2),
                                event.message.quote()
                            )
                        )
                    }
                }
            }
            GlobalEventChannel.parentScope(this).subscribeAlways<FriendMessageEvent> { event ->
                run {
                    if (sender.permitteeId.hasPermission(chatPermission) && (event.message[1] is PlainText)) {
                        event.sender.sendMessage(
                            chatEventHandler(
                                sender.id,
                                event.message.contentToString(),
                                event.message.quote()
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onDisable() {
        Client.close()
        logger.info { "MPSMC BotGPT unloaded" }
    }
}


