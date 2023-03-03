package cn.mpsmc

import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import net.mamoe.mirai.message.data.QuoteReply
import kotlin.math.absoluteValue

fun chatEventHandler(idL: Long, message: String, quote: QuoteReply): MessageChain {
    val id = idL.toString()
    if (Data.lastChatTime[id] != null)
        if (Config.expirationTime != -1 && ((System.currentTimeMillis() / 1000)) - Data.lastChatTime[id]!! >= Config.expirationTime)
            Data.historicalMessages.remove(id)
    if (message == "清空会话")
        return MessageChainBuilder().append(quote)
            .append(if (Data.historicalMessages.remove(id) != null) "清空成功" else "清空失败").build()
    else {
        val cd = checkCoolDown(id)
        BotGPT.logger.info(cd.toString())
        if (cd >= 0) {
            var finalMessage = ""
            var finalMessages: MutableList<String> = mutableListOf()
            val historicalMessages = Data.historicalMessages[id]
            Data.historicalMessages.remove(id)
            finalMessages.add(message)
            if (historicalMessages != null) {
                finalMessages = (historicalMessages + message).toMutableList()
            }
            for (i in finalMessages) {
                finalMessage += i + Config.separator
            }
            finalMessage = finalMessage.substring(0 until finalMessage.length - Config.separator.length)
            Data.lastChatTime[id] = System.currentTimeMillis() / 1000
            Data.historicalMessages[id] = finalMessages
            BotGPT.logger.info("$id -> $finalMessage")
            return try {
                val response =
                    Client.sendRequest(RequestObject(Config.model, listOf(Messages("user", finalMessage))))
                val content = (response.choices[0].message.content).trim()
                MessageChainBuilder().append(quote).append(PlainText(content)).build()
            } catch (e: Exception) {
                MessageChainBuilder().append(quote).append(PlainText("内部错误")).build()
            }
        } else
            return MessageChainBuilder().append(quote).append(PlainText("冷却中 [${checkCoolDown(id).absoluteValue}]"))
                .build()
    }
}