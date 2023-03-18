package cn.mpsmc

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object Config : AutoSavePluginConfig("config") {
    val openAI: OpenAI by value()
    val client: ProxyClient by value()
    val messages: Messages by value()
    val cooldown by value(10)
    val expirationTime by value(300)
    val maxCharacters by value(2000)
    val clearCommand by value("清空会话")
}

@Serializable
data class OpenAI(
    val apiUrl: String = "https://api.openai.com/v1/chat/completions",
    val token: String = "OpenAIToken",
    val model: String = "gpt-3.5-turbo"
)

@Serializable
data class ProxyClient(
    val timeout: Long = 100000,
    val socksProxyUrl: String = "",
    val socksProxyPort: Int = 0,
    val httpProxyUrl: String = ""
)

@Serializable
data class Messages(val errorMessage: String = "内部错误", val cooldownMessage: String = "冷却中 [%cd%]")