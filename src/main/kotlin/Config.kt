package cn.mpsmc

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    val apiUrl by value("https://api.openai.com/v1/chat/completions")
    val httpProxyUrl by value("")
    val socksProxyUrl by value("")
    val socksProxyPort by value(0)
    val token by value("ChatGPTToken")
    val expirationTime by value(60)
    val cooldown by value(30)
    val model by value("gpt-3.5-turbo")
    val separator by value(" ")
}