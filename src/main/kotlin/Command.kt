package cn.mpsmc

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote

fun chatHandler(source: String, message: String): List<String> {
    val userData = UserDataHandler(source)
    if (message == Config.clearCommand)
        return listOf(if (userData.removeFromHistoricalMessages()) Config.messages.clearSucceed else Config.messages.clearFailed)
    else {
        return if (userData.cooldown <= 0) {
            if (Config.singleQuestion && userData.getHistoricalMessages().lastOrNull()?.role == "user")
                return listOf(Config.messages.waitForReply)
            userData.addElementToHistoricalMessages(Message("user", message))
            userData.updateLastChatTime()
            try {
                val response =
                    Client().sendRequest(RequestObject(Config.openAI.model, userData.getHistoricalMessages().toList()))
                val c = (response.choices[0].message.content).trim()
                userData.addElementToHistoricalMessages(Message("assistant", c))
                userData.updateLastChatTime()
                c.split('\n')
            } catch (e: Exception) {
                BotGPT.logger.warning(e.message)
                userData.removeFromHistoricalMessages()
                listOf(Config.messages.errorMessage)
            }
        } else
            return listOf(Config.messages.cooldownMessage.replace("%cd%", userData.cooldown.toString()))
    }
}

fun chatEventHandler(context: Contact, event: MessageEvent): MessageChain {
    val message: String
    val source: String
    if (context is User) {
        message = event.message.contentToString()
        source = 'u' + context.id.toString()
    } else {
        message = event.message.contentToString().removeRange(0 until event.bot.id.toString().length + 2)
        source = 'g' + context.id.toString()
    }
    val apiReply = chatHandler(source, message)
    return if (apiReply.size > 1)
        buildForwardMessage(context, ForwardMessage.DisplayStrategy.Default) {
            for (i in apiReply)
                if (i.isNotEmpty())
                    event.bot says PlainText(i)
        }.toMessageChain()
    else
        event.message.quote() + PlainText(apiReply[0]).toMessageChain()
}